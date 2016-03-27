/**
 * Copyright 2016 Guoqiang Chen, Shanghai, China. All rights reserved.
 *
 *   Author: Guoqiang Chen
 *    Email: subchen@gmail.com
 *   WebURL: https://github.com/subchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package shaft.dao.metadata;

import jetbrick.util.builder.ToStringBuilder;

public final class DbColumn {
    private String name;
    private String typeName;
    private int typeLength;
    private int typePrecision;
    private boolean nullable;
    private String defaultValue;

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }

    public Integer getTypeLength() {
        return typeLength;
    }

    public Integer getTypePrecision() {
        return typePrecision;
    }

    public boolean isNullable() {
        return nullable;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflection(this);
    }

    public static class Builder {
        private final DbColumn obj = new DbColumn();

        public Builder() {
            obj.typeLength = -1;
            obj.typePrecision = -1;
        }

        public void setName(String name) {
            obj.name = name;
        }

        public void setTypeName(String typeName) {
            obj.typeName = typeName;
        }

        public void setTypeLength(int typeLength) {
            obj.typeLength = typeLength;
        }

        public void setTypePrecision(int typePrecision) {
            obj.typePrecision = typePrecision;
        }

        public void setNullable(boolean nullable) {
            obj.nullable = nullable;
        }

        public void setDefaultValue(String defaultValue) {
            obj.defaultValue = defaultValue;
        }

        public DbColumn build() {
            return obj;
        }
    }
}
