package de.atennert.connector.packets.enocean;

import java.util.Date;

import de.atennert.connector.packets.IPacketConstants;
import de.atennert.connector.packets.Packet;

/**
 * This class implements the EnOcean defined response packet (type:
 * {@link IPacketConstants#TYPE_RESPONSE}). Response packets always have a
 * response code and may include additional information. All common response
 * codes are available as constants in this class. They are:
 * <ul>
 * <li>{@link #RET_OK}</li>
 * <li>{@link #RET_ERROR}</li>
 * <li>{@link #RET_NOT_SUPPORTED}</li>
 * <li>{@link #RET_WRONG_PARAM}</li>
 * <li>{@link #RET_OPERATION_DENIED}</li>
 * </ul>
 * 
 * @author Andreas Tennert
 */
public class ResponsePacket extends Packet {

    // response codes
    /** OK ... command is understood and triggered */
    public static final int RET_OK = 0;
    /** There is an error occurred */
    public static final int RET_ERROR = 1;
    /** The functionality is not supported by that implementation */
    public static final int RET_NOT_SUPPORTED = 2;
    /** There was a wrong parameter in the command */
    public static final int RET_WRONG_PARAM = 3;
    /** Example: memory access denied (code-protected) */
    public static final int RET_OPERATION_DENIED = 4;

    /**
     * General constructor
     * 
     * @param data the required data part, contains the repsonse code and
     *            additional information if necessary
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public ResponsePacket( int[] data, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_RESPONSE, data, new int[0], timestamp, isValid );
    }

    /**
     * Constructor with separated response code and additional information.
     * 
     * @param responseCode the response code, those codes are available as
     *            constants, for instance {@link #RET_OK}
     * @param information additional information
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public ResponsePacket( int responseCode, int[] information, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_RESPONSE, new int[information.length + 1], new int[0], timestamp, isValid );

        this.data[0] = responseCode;
        System.arraycopy( information, 0, this.data, 1, information.length );
    }

    /**
     * Constructor for responses without further data
     * 
     * @param responseCode the response code, those codes are available as
     *            constants, for instance {@link #RET_OK}
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public ResponsePacket( int responseCode, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_RESPONSE, new int[] { responseCode }, new int[0], timestamp, isValid );
    }

    /**
     * @return the response code, for instance {@link #RET_OK}
     */
    public int getResponseCode() {
        return data[0];
    }
}
