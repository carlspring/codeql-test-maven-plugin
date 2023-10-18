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

/**
 * @author carlspring
 */
public class TestOptions
{

    private boolean generate = true;

    private String sourceVersion = "17";

    private String qlRef;


    public TestOptions()
    {
    }

    public boolean isGenerate()
    {
        return generate;
    }

    public void setGenerate(boolean generate)
    {
        this.generate = generate;
    }

    public String getSourceVersion()
    {
        return sourceVersion;
    }

    public void setSourceVersion(String sourceVersion)
    {
        this.sourceVersion = sourceVersion;
    }

    public String getQlRef()
    {
        return qlRef;
    }

    public void setQlRef(String qlRef)
    {
        this.qlRef = qlRef;
    }
}
