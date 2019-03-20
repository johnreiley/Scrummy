package com.threeguys.scrummy;

import android.content.Context;

import java.util.List;

/**
 * An interface that handles loading a list of sessions from file.
 */
public interface Load {

    /**
     * Load a SessionList class using the given context.
     * @param context The context of the class containing the Load class when load() is called.
     * @return A SessionList class holding the list of sessions found within the loaded file.
     */
    SessionList load(Context context);
}
