/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.org.gradle.language.javascript.internal

import org.gradle.api.internal.file.FileResolver
import org.gradle.language.base.sources.BaseLanguageSourceSet
import org.gradle.language.javascript.JavaScriptSourceSet
import org.gradle.language.javascript.internal.DefaultJavaScriptSourceSet
import spock.lang.Specification

class DefaultJavaScriptSourceSetTest extends Specification {
    def "has useful String representation"() {
        def sourceSet = BaseLanguageSourceSet.create(JavaScriptSourceSet, DefaultJavaScriptSourceSet, "javascriptX", "playX", Stub(FileResolver))

        expect:
        sourceSet.displayName == "JavaScript source 'playX:javascriptX'"
        sourceSet.toString() == "JavaScript source 'playX:javascriptX'"
    }
}
