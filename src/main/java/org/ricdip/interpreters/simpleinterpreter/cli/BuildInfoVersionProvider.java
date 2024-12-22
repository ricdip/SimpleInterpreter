package org.ricdip.interpreters.simpleinterpreter.cli;

import picocli.CommandLine;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class BuildInfoVersionProvider implements CommandLine.IVersionProvider {
    private static final String BUILD_INFO_RESOURCE_PATH = "META-INF/build-info.properties";
    private static final String BUILD_INFO_VERSION_PROPERTY = "build.version";

    /**
     * get SemVer string from 'META-INF/build-info.properties'
     *
     * @return SemVer string that describes the application version
     * @throws Exception if an error occurs
     */
    @Override
    public String[] getVersion() throws Exception {
        URL url = getClass().getClassLoader().getResource(BUILD_INFO_RESOURCE_PATH);
        if (url == null) {
            return new String[]{"no version found"};
        }
        try (InputStream resourceInputStream = url.openStream()) {
            Properties properties = new Properties();
            properties.load(resourceInputStream);
            return new String[]{properties.getProperty(BUILD_INFO_VERSION_PROPERTY)};
        }
    }
}
