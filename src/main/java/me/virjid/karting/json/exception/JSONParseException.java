package me.virjid.karting.json.exception;

/**
 * @author Virjid
 */
public class JSONParseException extends RuntimeException {
    private static final long serialVersionUID = -7156773771129816531L;

    public JSONParseException(String message) {
        super(message);
    }
}
