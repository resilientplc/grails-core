package org.grails.plugins.web

import grails.core.DefaultGrailsApplication
import grails.plugins.DefaultGrailsPluginManager

import org.apache.commons.logging.Log
import org.grails.compiler.injection.GrailsAwareTraitInjectionOperation

class LoggingGrailsPluginTests extends AbstractGrailsPluginTests {

    def controllerClass
    def serviceClass
    def taglibClass

    protected void onSetUp() {
        GrailsAwareTraitInjectionOperation.clearExtendedClasses()
        
        controllerClass = gcl.parseClass("""
        @grails.artefact.Artefact('Controller')
        class TestController {}""")
        serviceClass = gcl.parseClass("""
        @grails.artefact.Artefact('Service')
        class TestService {}""")
        taglibClass = gcl.parseClass("""
        @grails.artefact.Artefact('TagLib')
        class TestTagLib {}""")

        pluginsToLoad << gcl.loadClass("org.grails.plugins.CoreGrailsPlugin")
        pluginsToLoad << gcl.loadClass("org.grails.plugins.logging.log4j.LoggingGrailsPlugin")
    }

    void testLoggingPluginBeforeCore() {
        def pluginManager = new DefaultGrailsPluginManager([] as Class[], new DefaultGrailsApplication())

        pluginManager.loadPlugins()

        def core =  pluginManager.getGrailsPlugin("core")
        def logging =  pluginManager.getGrailsPlugin("logging")

        assertTrue "logging plugin should have loaded before core",
            pluginManager.pluginList.indexOf(core) > pluginManager.pluginList.indexOf(logging)
    }

    void testLogAvailableToController() {
        def registry = GroovySystem.metaClassRegistry
        def controller = controllerClass.newInstance()
        assertTrue controller.log instanceof Log
    }

    void testLogAvailableToService() {
        def registry = GroovySystem.metaClassRegistry
        def service = serviceClass.newInstance()
        assertTrue service.log instanceof Log
    }

    void testLogAvailableToTagLib() {
        def registry = GroovySystem.metaClassRegistry
        def taglib = taglibClass.newInstance()
        assertTrue taglib.log instanceof Log
    }
}