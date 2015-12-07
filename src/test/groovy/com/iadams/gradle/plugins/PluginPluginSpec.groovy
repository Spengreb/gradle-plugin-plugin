/*
 * Gradle Plugin Plugin
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Iain Adams
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.iadams.gradle.plugins

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class PluginPluginSpec extends Specification {

  static final String PLUGIN_ID = 'com.iadams.gradle-plugin-plugin'
  Project project

  @Rule
  TemporaryFolder projectDir

  def setup() {
    project = ProjectBuilder.builder().build()
    project.pluginManager.apply PLUGIN_ID
  }

  @Unroll
  def "the plugin has the task: #task"() {
    expect:
    project.tasks.findByName(task)

    where:
    task << ['integTest', 'jacocoTestReport', 'jacocoIntegTestReport', 'sonarqube', 'publishPlugins']
  }

  def "has correct sourceSets"() {
    expect:
    project.sourceSets.findByName('integTest')
  }

  def 'apply does not throw exceptions'() {
    when:
    project.apply plugin: PLUGIN_ID

    then:
    noExceptionThrown()
  }

  def 'apply is idempotent'() {
    when:
    project.apply plugin: PLUGIN_ID
    project.apply plugin: PLUGIN_ID

    then:
    noExceptionThrown()
  }

  def 'apply is fine on all levels of multiproject'() {
    def sub = createSubproject(project, 'sub')
    project.subprojects.add(sub)

    when:
    project.apply plugin: PLUGIN_ID
    sub.apply plugin: PLUGIN_ID

    then:
    noExceptionThrown()
  }

  def 'apply to multiple subprojects'() {
    def subprojectNames = ['sub1', 'sub2', 'sub3']

    subprojectNames.each { subprojectName ->
      def subproject = createSubproject(project, subprojectName)
      project.subprojects.add(subproject)
    }

    when:
    project.apply plugin: PLUGIN_ID

    subprojectNames.each { subprojectName ->
      def subproject = project.subprojects.find { it.name == subprojectName }
      subproject.apply plugin: PLUGIN_ID
    }

    then:
    noExceptionThrown()
  }

  Project createSubproject(Project parentProject, String name) {
    ProjectBuilder.builder().withName(name).withProjectDir(new File(projectDir.root, name)).withParent(parentProject).build()
  }
}
