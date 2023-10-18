/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.carlspring.maven.plugins.codeql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.plugins.dependency.fromDependencies.BuildClasspathMojo;

/**
 * @author carlspring
 * @since 1.0.0
 */
@Mojo(name = "generate",
      requiresDependencyResolution = ResolutionScope.TEST,
      defaultPhase = LifecyclePhase.GENERATE_SOURCES,
      threadSafe = true)
public class GenerateCodeQLTestResourcesMojo
        extends BuildClasspathMojo
{

    @Parameter(property = "mdep.fileSeparator",
               defaultValue = "")
    private String fileSeparator;
    @Parameter(property = "mdep.pathSeparator",
               defaultValue = "")
    private String pathSeparator;

    @Parameter(property = "mdep.localRepoProperty",
               defaultValue = "")
    private String localRepoProperty;

    private TestOptions testOptions;


    /**
     * Main entry into mojo. Gets the list of dependencies and iterates to create a classpath.
     *
     * @throws MojoExecutionException with a message if an error occurs.
     * @see #getResolvedDependencies(boolean)
     */
    @Override
    protected void doExecute()
            throws MojoExecutionException
    {
        String classpath = assembleClasspath();

        try
        {
            // TODO: 1) Generate the classpath string
            // TODO: 2) Generate the `testOptions` file
            generateOptionsFile(classpath);
            // TODO: 3) Generate a `.qlref` file
            generateQLRefFile();
        }
        catch (Exception e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public void generateOptionsFile(String classpath)
            throws IOException
    {
        String options = "//semmle-extractor-testOptions: --javac-args -cp " + classpath +
                         " -source " + (testOptions != null && testOptions.getQlRef() != null ?
                                        testOptions.getSourceVersion() :
                                        "17");
        File f = new File("options");

        try (FileOutputStream fos = new FileOutputStream(f))
        {
            fos.write(options.getBytes());
            fos.write("\n".getBytes());

            getLog().info("Generated '" + f.getAbsolutePath() + "' file.");
        }
    }

    public void generateQLRefFile()
            throws IOException
    {
        // QL Ref file name ends with `.qlref` and contains the name of the CodeQL query file (including the extension)
        // For example, if the CodeQL query file is `MyQuery.ql`, then the `.qlref` file will
        // be `MyQuery.qlref` and will contain the text 'MyQuery.ql'.

        String basedir = System.getProperty("user.dir");
        String[] dirs = basedir.split(String.valueOf(File.separatorChar));

        String qlFileName = dirs[dirs.length - 1] + ".ql";
        String qlRefFileName = dirs[dirs.length - 1] + ".qlref";

        File f = new File(qlRefFileName);

        try (FileOutputStream fos = new FileOutputStream(f))
        {
            fos.write(qlFileName.getBytes());
            fos.write("\n".getBytes());

            getLog().info("Generated '" + f.getAbsolutePath() + "' file.");
        }
    }

    public String assembleClasspath()
            throws MojoExecutionException
    {
        boolean isPathSepSet = pathSeparator != null && !pathSeparator.isEmpty();

        Set<Artifact> artifacts = this.getResolvedDependencies(true);
        if (artifacts == null || artifacts.isEmpty())
        {
            this.getLog().info("No dependencies found.");
        }

        List<Artifact> artList = new ArrayList<>(artifacts);
        StringBuilder sb = new StringBuilder();
        Iterator<Artifact> i = artList.iterator();
        if (i.hasNext())
        {
            this.appendArtifactPath(i.next(), sb);

            while (i.hasNext())
            {
                sb.append(isPathSepSet ? this.pathSeparator : File.pathSeparator);
                this.appendArtifactPath(i.next(), sb);
            }
        }

        return sb.toString();
    }

}
