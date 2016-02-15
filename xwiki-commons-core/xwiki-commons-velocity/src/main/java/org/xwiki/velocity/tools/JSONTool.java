/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.velocity.tools;

import java.beans.Transient;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker.Std;
import com.fasterxml.jackson.databind.module.SimpleModule;

import net.sf.json.JSON;
import net.sf.json.JSONException;
import net.sf.json.JSONSerializer;

/**
 * Velocity tool to facilitate serialization of Java objects to the JSON format.
 *
 * @version $Id$
 * @since 4.0M2
 */
public class JSONTool
{
    /**
     * Used to workaround Jackson limitation.
     * <ul>
     * <li>Jackson does not take into account java.beans.Transient
     * (https://github.com/FasterXML/jackson-databind/issues/857)</li>
     * </ul>
     */
    private static class CustomVisibilityChecker extends Std
    {
        protected static final CustomVisibilityChecker CUSTOM = new CustomVisibilityChecker(
            Std.class.getAnnotation(JsonAutoDetect.class));

        public CustomVisibilityChecker(JsonAutoDetect ann)
        {
            super(ann);
        }

        @Override
        public boolean isCreatorVisible(AnnotatedMember m)
        {
            if (m.getAnnotation(Transient.class) != null) {
                return false;
            }

            return super.isCreatorVisible(m);
        }

        @Override
        public boolean isFieldVisible(Field f)
        {
            if (f.getAnnotation(Transient.class) != null) {
                return false;
            }

            return super.isFieldVisible(f);
        }

        @Override
        public boolean isGetterVisible(Method m)
        {
            if (m.getAnnotation(Transient.class) != null) {
                return false;
            }

            return super.isGetterVisible(m);
        }

        @Override
        public boolean isIsGetterVisible(Method m)
        {
            if (m.getAnnotation(Transient.class) != null) {
                return false;
            }

            return super.isIsGetterVisible(m);
        }

        @Override
        public boolean isSetterVisible(Method m)
        {
            if (m.getAnnotation(Transient.class) != null) {
                return false;
            }

            return super.isSetterVisible(m);
        }
    }

    /** Logging helper object. */
    private Logger logger = LoggerFactory.getLogger(JSONTool.class);

    /**
     * Serialize a Java object to the JSON format.
     * <p>
     * Examples:
     * <ul>
     * <li>numbers and boolean values: 23, 13.5, true, false</li>
     * <li>strings: "one\"two'three" (quotes included)</li>
     * <li>arrays and collections: [1, 2, 3]</li>
     * <li>maps: {"number": 23, "boolean": false, "string": "value"}</li>
     * <li>beans: {"enabled": true, "name": "XWiki"} for a bean that has #isEnabled() and #getName() getters</li>
     * </ul>
     *
     * @param object the object to be serialized to the JSON format
     * @return the JSON-verified string representation of the given object
     */
    public String serialize(Object object)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibilityChecker(CustomVisibilityChecker.CUSTOM);
            SimpleModule m = new SimpleModule("org.json.* serializer", new Version(1, 0, 0, "", "org.json", "json"));
            m.addSerializer(JSONObject.class, new JSONObjectSerializer());
            m.addSerializer(JSONArray.class, new JSONArraySerializer());
            mapper.registerModule(m);
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            this.logger.error("Failed to serialize object to JSON", e);
        }

        return null;
    }

    /**
     * Parse a serialized JSON into a real JSON object. Only valid JSON strings can be parsed, and doesn't support
     * JSONP. If the argument is not valid JSON, then {@code null} is returned.
     *
     * @param json the string to parse, must be valid JSON
     * @return the parsed JSON, either a {@link net.sf.json.JSONObject} or a {@link net.sf.json.JSONArray}, or
     *         {@code null} if the argument is not a valid JSON
     * @since 5.2M1
     */
    // FIXME: directly returning in a public API the object of a dead library, not very nice for something introduced in
    // 5.2...
    public JSON parse(String json)
    {
        try {
            return JSONSerializer.toJSON(json);
        } catch (JSONException ex) {
            this.logger.info("Tried to parse invalid JSON: [{}], exception was: {}", StringUtils.abbreviate(json, 32),
                ex.getMessage());
            return null;
        }
    }

    class JSONObjectSerializer extends JsonSerializer<JSONObject>
    {
        @Override
        public void serialize(JSONObject value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException
        {
            jgen.writeRawValue(value.toString());
        }
    }

    class JSONArraySerializer extends JsonSerializer<JSONArray>
    {
        @Override
        public void serialize(JSONArray value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException
        {
            jgen.writeRawValue(value.toString());
        }
    }
}
