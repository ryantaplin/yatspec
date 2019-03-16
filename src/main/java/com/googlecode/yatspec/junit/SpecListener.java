package com.googlecode.yatspec.junit;

import com.googlecode.yatspec.plugin.sequencediagram.SequenceDiagramGenerator;
import com.googlecode.yatspec.plugin.sequencediagram.SvgWrapper;
import com.googlecode.yatspec.rendering.ScenarioNameRenderer;
import com.googlecode.yatspec.rendering.ScenarioNameRendererFactory;
import com.googlecode.yatspec.rendering.html.DontHighlightRenderer;
import com.googlecode.yatspec.rendering.html.HtmlResultRenderer;
import com.googlecode.yatspec.state.Result;
import com.googlecode.yatspec.state.Scenario;
import com.googlecode.yatspec.state.ScenarioName;
import com.googlecode.yatspec.state.TestResult;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import com.googlecode.yatspec.state.givenwhenthen.WithTestState;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.engine.execution.AfterEachMethodAdapter;
import org.junit.jupiter.engine.extension.ExtensionRegistry;
import org.junit.platform.commons.util.Preconditions;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class SpecListener implements AfterAllCallback, AfterEachMethodAdapter, TestExecutionExceptionHandler {

    private Map<String, Scenario> currentScenario = new HashMap<>();
    private Map<Method, Throwable> failures = new HashMap<>();
    private Object testInstance;
    private Result testResult;
    private MethodNameWithArguments methodNameWithArguments = new MethodNameWithArguments();


    @Override
    public void invokeAfterEachMethod(ExtensionContext extensionContext, ExtensionRegistry extensionRegistry) throws Throwable {
        testInstance = extensionContext.getRequiredTestInstance();
        if (testResult == null) {
            testResult = new TestResult(testInstance.getClass());
        }

        String nameWithArguments = methodNameWithArguments.createFrom(extensionContext.getRequiredTestMethod());

        String fullyQualifiedTestMethod = testInstance.getClass().getCanonicalName() + "." + nameWithArguments;
        Scenario scenario = testResult.getScenario(nameWithArguments);
        Throwable failure = failures.remove(extensionContext.getRequiredTestMethod());
        if (failure != null) {
            scenario.setException(failure);
        }
        currentScenario.put(fullyQualifiedTestMethod, scenario);

        if (testInstance instanceof WithTestState) {
            TestState testState = ((WithTestState) testInstance).testState();
            currentScenario.get(fullyQualifiedTestMethod).setTestState(testState);
        }
    }

    @Override
    public void handleTestExecutionException(ExtensionContext extensionContext, Throwable failure) throws Throwable {
        failures.put(extensionContext.getRequiredTestMethod(), failure);
        throw failure;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        resultListeners(testInstance).complete(testResult);
    }

    private WithCustomResultListeners resultListeners(Object testInstance) {
        if (testInstance instanceof WithCustomResultListeners) {
            return (WithCustomResultListeners) testInstance;
        } else if (hasSequenceDiagramExtension(testInstance)) {
            return defaultSequenceDiagramResultListener();
        } else {
            return new DefaultResultListeners();
        }
    }

    private WithCustomResultListeners defaultSequenceDiagramResultListener() {
        return () -> List.of(
                new HtmlResultRenderer().
                        withCustomHeaderContent(SequenceDiagramGenerator.getHeaderContentForModalWindows()).
                        withCustomRenderer(SvgWrapper.class, new DontHighlightRenderer()));
    }

    private boolean hasSequenceDiagramExtension(Object testInstance) {
        ExtendWith annotation = testInstance.getClass().getAnnotation(ExtendWith.class);
        return asList(annotation.value()).contains(SequenceDiagramExtension.class);
    }

    private static class MethodNameWithArguments {
        private final Map<String, Integer> rowIndexMap = new HashMap<>();
        private final ScenarioNameRenderer scenarioNameRenderer = ScenarioNameRendererFactory.renderer();

        private String createFrom(Method requiredTestMethod) {
            final String name = requiredTestMethod.getName();
            Table annotation = requiredTestMethod.getAnnotation(Table.class);
            if (annotation == null) {
                return name;
            }

            rowIndexMap.computeIfPresent(name, (name1, rowIndex) -> ++rowIndex);
            rowIndexMap.putIfAbsent(name, 0);

            Integer rowIndex = rowIndexMap.get(name);

            Row[] rows = annotation.value();
            Preconditions.condition(rowIndex < rows.length, format("@Table had %d @Rows but index was %d", rows.length, rowIndex));

            return scenarioNameRenderer.render(new ScenarioName(name, asList(rows[rowIndex].value())));
        }
    }
}
