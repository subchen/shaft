/**
 * Copyright 2016 Guoqiang Chen, Shanghai, China. All rights reserved.
 * <p>
 * Author: Guoqiang Chen
 * Email: subchen@gmail.com
 * WebURL: https://github.com/subchen
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package shaft.dao;

public interface PageInfo {

    /**
     * 第几页（从 1 开始）
     */
    int getPageNo();

    /**
     * 每页大小
     */
    int getPageSize();

    /**
     * 总记录数（可以缓存上次分页计算出的结果）
     *
     * @return 0 代表需要重新计算
     */
    int getTotalCount();

    /**
     * 分页 URL
     */
    String getPageUrl();
}
