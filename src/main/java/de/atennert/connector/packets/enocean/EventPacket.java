package de.atennert.connector.packets.enocean;

import java.util.Date;

import de.atennert.connector.packets.IPacketConstants;
import de.atennert.connector.packets.Packet;

/**
 * This class implements the EnOcean defined event packet (type:
 * {@link IPacketConstants#TYPE_EVENT}). Event packets always have an event code
 * and may include additional information. All event codes are available as
 * constants in this class. They are:
 * <ul>
 * <li>{@link #SA_RECLAIM_NOT_SUCCESSFUL}</li>
 * <li>{@link #SA_CONFIRM_LEARN}</li>
 * <li>{@link #SA_LEARN_ACK}</li>
 * <li>{@link #CO_READY}</li>
 * <li>{@link #CO_EVENT_SECUREDEVICES}</li>
 * </ul>
 * 
 * @author Andreas Tennert
 */
public class EventPacket extends Packet {

    /** Informs the backbone of a Smart Ack Client to not successful reclaim. */
    public final static int SA_RECLAIM_NOT_SUCCESSFUL = 1;
    /** Used for SMACK to confirm/discard learn in/out */
    public final static int SA_CONFIRM_LEARN = 2;
    /** Inform backbone about result of learn request */
    public final static int SA_LEARN_ACK = 3;
    /** Inform backbone about the readiness for operation */
    public final static int CO_READY = 4;
    /** Informs about a secure device */
    public final static int CO_EVENT_SECUREDEVICES = 5;

    /**
     * General constructor.
     * 
     * @param data the required data part, contains the event code and
     *            additional information if necessary
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public EventPacket( int[] data, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_EVENT, data, new int[0], timestamp, isValid );
    }

    /**
     * Constructor with separated event code and additional information.
     * 
     * @param eventCode the event code, those codes are available as constants,
     *            for instance {@link #SA_RECLAIM_NOT_SUCCESSFUL}
     * @param information additional information
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public EventPacket( int eventCode, int[] information, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_EVENT, new int[information.length + 1], new int[0], timestamp, isValid );

        this.data[0] = eventCode;
        System.arraycopy( information, 0, this.data, 1, information.length );
    }

    /**
     * Constructor for event messages without additional information
     * 
     * @param eventCode the event code, those codes are available as constants,
     *            for instance {@link #SA_RECLAIM_NOT_SUCCESSFUL}
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public EventPacket( int eventCode, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_EVENT, new int[] { eventCode }, new int[0], timestamp, isValid );
    }

    /**
     * @return the event code, for instance {@link #SA_RECLAIM_NOT_SUCCESSFUL}
     */
    public int getEventCode() {
        return data[0];
    }
}
