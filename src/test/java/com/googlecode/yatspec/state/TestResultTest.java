package com.googlecode.yatspec.state;

import com.googlecode.yatspec.junit.Notes;
import org.junit.Test;

import java.lang.annotation.Annotation;

import static com.googlecode.yatspec.junit.Notes.methods.notes;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Notes("Some notes")
public class TestResultTest {
    @Test
    public void supportsNotesOnClass() {
        Annotation[] annotations = getClass().getAnnotations();
        assertThat(notes(asList(annotations)).get().value(), is("Some notes"));
    }
}
