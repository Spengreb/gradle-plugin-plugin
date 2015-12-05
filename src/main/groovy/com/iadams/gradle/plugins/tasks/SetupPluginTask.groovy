package com.iadams.gradle.plugins.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author iwarapter
 */
class SetupPluginTask extends DefaultTask {

  static final PLUGIN_DESCRIPTOR_PATH = 'src/main/resources/META-INF/gradle-plugins'
  static final PLUGIN_CLASS_PATH = 'src/main/groovy'

  @TaskAction
  void setupPlugin(){

    project.file(PLUGIN_DESCRIPTOR_PATH).mkdirs()
    project.file("${PLUGIN_CLASS_PATH}/${project.group}").mkdirs()

    def pluginDescriptor = project.file("${PLUGIN_DESCRIPTOR_PATH}/${project.group}.${project.name}.properties")
    pluginDescriptor << "implementation-class=${project.group}.MyPlugin"

    def pluginClass = project.file("${PLUGIN_CLASS_PATH}/${project.group}/MyPlugin.groovy")
    pluginClass << """package ${project.group}

                      import org.gradle.api.Plugin
                      import org.gradle.api.Project

                      public class MyPlugin implements Plugin<Project>  {

                        @Override
                        void apply(Project project) {}

                      }
                      """.stripIndent()
  }
}
