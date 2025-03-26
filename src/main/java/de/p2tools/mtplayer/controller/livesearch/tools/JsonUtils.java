package de.p2tools.mtplayer.controller.livesearch.tools;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Arrays;
import java.util.Optional;

public final class JsonUtils {

    private JsonUtils() {
        super();
    }

    /**
     * Checks if a JSON tree path outgoing from the first JsonElement is correct
     * and all given element IDs exist in the given order.<br>
     * Example: If the tree is MainObj {@literal -> } FirstChild {@literal -> }
     * SecondChild and the given JsonElement is MainObj and the element IDs are
     * "FirstChild,SecondChild" this will return true.
     *
     * @param aJsonElement The first element from which the tree will be checked.
     * @param aElementIds  The tree-childs in the order in which they should be
     *                     checked.
     * @return true if the JSON tree path is correct and all given element IDs
     * exist in the given order.
     */
    public static boolean checkTreePath(
            final JsonNode aJsonElement, final String... aElementIds) {
        JsonNode elemToCheck = aJsonElement;
        for (final String elementId : aElementIds) {
            if (elemToCheck != null && elemToCheck.has(elementId)) {
                elemToCheck = elemToCheck.get(elementId);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the value of an attribute
     *
     * @param aJsonObject    the object
     * @param aAttributeName the name of the attribute
     * @return the value of the attribute, if it exists, else Optional.empty
     */
    public static Optional<String> getAttributeAsString(
            final JsonNode aJsonObject, final String aAttributeName) {

        if (aJsonObject.has(aAttributeName)) {
            final JsonNode aElement = aJsonObject.get(aAttributeName);
            if (aElement != null) {
                return Optional.of(aElement.asText());
            }
        }
        return Optional.empty();
    }

    public static Optional<Integer> getAttributeAsInt(final JsonNode jsonNode, final String attributeName) {
        if (jsonNode.has(attributeName)) {
            final JsonNode child = jsonNode.get(attributeName);
            if (child != null) {
                return Optional.of(child.asInt());
            }
        }

        return Optional.empty();
    }

    /**
     * Checks if the {@link JsonNode} has all given elements and if no element
     * is null.
     *
     * @param aJsonObject The object to check.
     * @param aElementIds The elements which it should has.
     * @return true when the object has all given elements and if no element is
     * null.
     */
    public static boolean hasElements(final JsonNode aJsonObject, final String... aElementIds) {

        for (final String elementId : aElementIds) {
            if (!aJsonObject.has(elementId) || aJsonObject.get(elementId) == null) {
                return false;
            }
        }
        return true;
    }

    public static Optional<Integer> getElementValueAsInteger(final JsonNode aJsonElement, final String... aElementIds) {
        Optional<JsonNode> rs = JsonUtils.getElement(aJsonElement, aElementIds);
        return rs.map(JsonNode::asInt);
    }

    /**
     * Checks if the {@link JsonNode} has all given elements and if no element
     * is null or empty.
     *
     * @param aJsonObject The object to check.
     * @param aElementIds The elements which it should has.
     * @return true when the object has all given elements and if no element is
     * null.
     */
    public static boolean hasStringElements(final JsonNode aJsonObject, final String... aElementIds) {
        return hasElements(aJsonObject, aElementIds)
                && Arrays.stream(aElementIds)
                .map(aJsonObject::get)
                .map(JsonNode::asText)
                .noneMatch(String::isEmpty);
    }

    public static Optional<String> getElementValueAsString(final JsonNode aJsonElement, final String... aElementIds) {
        Optional<JsonNode> rs = JsonUtils.getElement(aJsonElement, aElementIds);
        return rs.map(JsonNode::asText);
    }

    public static Optional<JsonNode> getElement(final JsonNode aJsonElement, final String... aElementIds) {
        Optional<JsonNode> rs = Optional.empty();

        if (aElementIds == null || aElementIds.length == 0 || aJsonElement == null) {
            return rs;
        }

        JsonNode aJsonObject = aJsonElement;
        for (int i = 0; i < aElementIds.length - 1; i++) {
            String elementId = aElementIds[i];
            if (aJsonObject.has(elementId) && aJsonObject.get(elementId) != null) {
                aJsonObject = aJsonObject.get(elementId);
            } else {
                aJsonObject = null;
                break;
            }
        }
        //
        String elementId = aElementIds[aElementIds.length - 1];
        if (aJsonObject != null && aJsonObject.has(elementId) && aJsonObject.get(elementId) != null) {
            rs = Optional.of(aJsonObject.get(elementId));
        }
        //
        return rs;
    }
}
