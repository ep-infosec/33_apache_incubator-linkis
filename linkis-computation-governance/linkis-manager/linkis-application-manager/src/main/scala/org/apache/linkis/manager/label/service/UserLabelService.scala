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

package org.apache.linkis.manager.label.service

import org.apache.linkis.manager.label.entity.Label

import java.util

trait UserLabelService {

  def addLabelToUser(user: String, labels: util.List[Label[_]]): Unit

  def addLabelToUser(user: String, label: Label[_]): Unit

  def removeLabelFromUser(user: String, labels: util.List[Label[_]]): Unit

  def getUserByLabel(label: Label[_]): util.List[String]

  def getUserByLabels(labels: util.List[Label[_]]): util.List[String]

  def getUserLabels(user: String): util.List[Label[_]]

}
