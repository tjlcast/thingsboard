/**
 * Copyright © 2016-2017 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.transport.http.utils;

/**
 * Created by Administrator on 2017/12/7.
 */
public class StringUtil {
    public static boolean checkNotNull(String... strs){
        boolean res = true;
        for(String s:strs){
            res = res&& checkNotNull(s);
        }
        return res;
    }
    public static boolean checkNotNull(String str){
        if(str!=null&&!"".equals(str)) return true;
        return false;
    }
}
