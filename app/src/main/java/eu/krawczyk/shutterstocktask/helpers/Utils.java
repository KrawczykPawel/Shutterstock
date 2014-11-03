/*
 * Copyright (C) 2012 The Andrino Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.krawczyk.shutterstocktask.helpers;

import java.util.ArrayList;
import java.util.List;

import eu.krawczyk.shutterstocktask.model.ImageFile;

/**
 * @author Paweł Krawczyk
 * @class The Utils Class.
 * <p/>
 * @since 31 oct, 2014.
 */
public class Utils {
    private Utils() {

    }

    public static List<ImageFile> multiplyList(List<ImageFile> listToDuplicate, int loops) {
        List<ImageFile> clonedList = new ArrayList<ImageFile>(listToDuplicate.size() * loops);
        for (int i = 0; i < loops; i++) {
            for (ImageFile item : listToDuplicate) {
                clonedList.add(item);
            }
        }
        return clonedList;
    }
}
