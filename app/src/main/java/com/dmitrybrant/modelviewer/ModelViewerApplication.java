package com.dmitrybrant.modelviewer;

import android.app.Application;
import android.support.annotation.Nullable;

/*
 * Copyright 2017 Dmitry Brant. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ModelViewerApplication extends Application
{
    private static ModelViewerApplication INSTANCE;

    // Store the current model globally, so that we don't have to re-decode it upon
    // relaunching the main or VR activities.
    // TODO: handle this a bit better.
    @Nullable private Model currentModel;
    private Model1 nextModel;
    private Model2 nextMode2;
    private Model3 nextMode3;
    public static ModelViewerApplication getInstance() {
        return INSTANCE;
    }

    @Nullable
    public Model getCurrentModel() {
        return currentModel;
    }

    public void setCurrentModel(@Nullable Model model) {
        currentModel = model;
    }


    @Nullable
    public Model1 getnextModel() {
        return nextModel;
    }

    public void setnextmodel(@Nullable Model1 model) {
        nextModel = model;
    }


    public Model2 getnextModel2() {
        return nextMode2;
    }

    public void setnextmode2(@Nullable Model2 model) {
        nextMode2 = model;
    }

    public Model3 getnextModel3() {
        return nextMode3;
    }

    public void setnextmode3(@Nullable Model3 model) {
        nextMode3 = model;
    }


    @Override
    public void onCreate()
    {
        super.onCreate();
        INSTANCE = this;
    }
}
