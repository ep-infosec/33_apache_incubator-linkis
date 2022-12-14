/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.linkis.entrance.utils

import org.apache.linkis.common.io.FsPath
import org.apache.linkis.common.utils.Utils
import org.apache.linkis.storage.FSFactory
import org.apache.linkis.storage.fs.FileSystem
import org.apache.linkis.storage.utils.{FileSystemUtils, StorageConfiguration, StorageUtils}

object CommonLogPathUtils {

  def buildCommonPath(commonPath: String): Unit = {
    val fileSystem = getRootFs(commonPath)
    fileSystem.init(null)
    val realPath: String = if (commonPath.endsWith("/")) {
      commonPath.substring(0, commonPath.length - 1)
    } else {
      commonPath
    }
    val fsPath = new FsPath(realPath)
    if (!fileSystem.exists(fsPath)) {
      FileSystemUtils.mkdirs(fileSystem, fsPath, StorageUtils.getJvmUser)
      fileSystem.setPermission(fsPath, "770")
    }
    Utils.tryQuietly(fileSystem.close())
  }

  def getRootFs(commonPath: String): FileSystem = {
    val fsPath = new FsPath(commonPath)
    if (StorageUtils.HDFS.equals(fsPath.getFsType)) {
      FSFactory.getFs(StorageUtils.HDFS).asInstanceOf[FileSystem]
    } else {
      FSFactory
        .getFs(StorageUtils.FILE, StorageConfiguration.LOCAL_ROOT_USER.getValue)
        .asInstanceOf[FileSystem]
    }
  }

}
