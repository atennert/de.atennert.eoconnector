package de.atennert.connector.packets.enocean;

import java.util.Date;

import de.atennert.connector.packets.IPacketConstants;
import de.atennert.connector.packets.Packet;

/**
 * This class implements the EnOcean defined smart ack command packet (type:
 * {@link IPacketConstants#TYPE_SMART_ACK_COMMAND}). Smart ack command packets
 * always have a command code and may include additional information. They don't
 * have optional data. All command codes are available as constants in this
 * class. They are:
 * <ul>
 * <li>{@link #SA_WR_LEARNMODE}</li>
 * <li>{@link #SA_RD_LEARNMODE}</li>
 * <li>{@link #SA_WR_LEARNCONFIRM}</li>
 * <li>{@link #SA_WR_CLIENTLEARNRQ}</li>
 * <li>{@link #SA_WR_RESET}</li>
 * <li>{@link #SA_RD_LEARNEDCLIENTS}</li>
 * <li>{@link #SA_WR_RECLAIMS}</li>
 * <li>{@link #SA_WR_POSTMASTER}</li>
 * </ul>
 * 
 * @author Andreas Tennert
 */
public class SmartAckCommandPacket extends Packet {

    /** Set/Reset Smart Ack learn mode */
    public static final int SA_WR_LEARNMODE = 1;
    /** Get Smart Ack learn mode state */
    public static final int SA_RD_LEARNMODE = 2;
    /** Used for Smart Ack to add or delete a mailbox of a client */
    public static final int SA_WR_LEARNCONFIRM = 3;
    /** Send Smart Ack Learn request (Client) */
    public static final int SA_WR_CLIENTLEARNRQ = 4;
    /** Send reset command to a Smart Ack client */
    public static final int SA_WR_RESET = 5;
    /** Get Smart Ack learned sensors / mailboxes */
    public static final int SA_RD_LEARNEDCLIENTS = 6;
    /** Set number of reclaim attempts */
    public static final int SA_WR_RECLAIMS = 7;
    /** Activate/Deactivate Post master functionality */
    public static final int SA_WR_POSTMASTER = 8;

    public SmartAckCommandPacket( int[] data, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_SMART_ACK_COMMAND, data, new int[0], timestamp, isValid );
    }

    /**
     * Preferred for use with {@link SmartAckCommandPacket#SA_WR_RESET}
     * 
     * @param commandCode the command code, for instance
     *            {@link #SA_WR_LEARNMODE}
     * @param data
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public SmartAckCommandPacket( int commandCode, int[] data, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_SMART_ACK_COMMAND, new int[data.length + 1], new int[0], timestamp, isValid );

        this.data[0] = commandCode;
        System.arraycopy( data, 0, this.data, 1, data.length );
    }

    /**
     * Use for {@link SmartAckCommandPacket#SA_WR_LEARNMODE}
     * 
     * @param enable
     * @param extended
     * @param timeout
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public SmartAckCommandPacket( int enable, int extended, int[] timeout, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_SMART_ACK_COMMAND, new int[] { SA_RD_LEARNMODE, enable, timeout[0], timeout[1],
                timeout[2], timeout[3] }, new int[0], timestamp, isValid );
    }

    /**
     * Use with {@link SmartAckCommandPacket#SA_RD_LEARNMODE} or
     * {@link SmartAckCommandPacket#SA_RD_LEARNEDCLIENTS}
     * 
     * @param commandCode the command code, for instance
     *            {@link #SA_WR_LEARNMODE}
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public SmartAckCommandPacket( int commandCode, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_SMART_ACK_COMMAND, new int[] { commandCode }, new int[0], timestamp, isValid );
    }

    /**
     * Use for {@link SmartAckCommandPacket#SA_WR_LEARNCONFIRM}
     * 
     * @param responseTime
     * @param confirmCode
     * @param postmasterCandidateID
     * @param smartAckClientID
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public SmartAckCommandPacket( int[] responseTime, int confirmCode, int[] postmasterCandidateID,
            int[] smartAckClientID, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_SMART_ACK_COMMAND, new int[12], new int[0], timestamp, isValid );

        this.data[0] = SA_WR_LEARNCONFIRM;
        System.arraycopy( responseTime, 0, this.data, 1, 2 );
        this.data[3] = confirmCode;
        System.arraycopy( postmasterCandidateID, 0, this.data, 4, 4 );
        System.arraycopy( smartAckClientID, 0, this.data, 8, 4 );
    }

    /**
     * Use for {@link SmartAckCommandPacket#SA_WR_CLIENTLEARNRQ}
     * 
     * @param eEP
     * @param manufacturerIDPlus 0b11111nnn: nnn = most significant 3 bits of
     *            manufacturer ID
     * @param manufacturerID
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public SmartAckCommandPacket( int[] eEP, int manufacturerIDPlus, int manufacturerID, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_SMART_ACK_COMMAND, new int[6], new int[0], timestamp, isValid );

        this.data[0] = SA_WR_CLIENTLEARNRQ;
        this.data[1] = manufacturerIDPlus;
        this.data[2] = manufacturerID;
        System.arraycopy( eEP, 0, data, 3, 3 );
    }

    /**
     * Use with {@link SmartAckCommandPacket#SA_WR_RECLAIMS} or
     * {@link SmartAckCommandPacket#SA_WR_POSTMASTER}
     * 
     * @param commandCode the command code, for instance
     *            {@link #SA_WR_LEARNMODE}
     * @param count Reclaim count / Mailbox count
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public SmartAckCommandPacket( int commandCode, int count, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_SMART_ACK_COMMAND, new int[] { commandCode, count }, new int[0], timestamp,
                isValid );
    }

    /**
     * @return the command code, for instance {@link #SA_WR_LEARNMODE}
     */
    public int getCommandCode() {
        return data[0];
    }
}
