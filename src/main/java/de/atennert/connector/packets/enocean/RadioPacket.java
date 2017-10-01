package de.atennert.connector.packets.enocean;

import java.util.Date;

import de.atennert.connector.packets.IPacketConstants;
import de.atennert.connector.packets.Packet;

/**
 * This class implements the EnOcean defined radio packet (type:
 * {@link IPacketConstants#TYPE_RADIO}). Radio packets contain required data and
 * may include optional data. The required data is structured as follows (may
 * differ in special cases):
 * <ul>
 * <li>R-ORG value</li>
 * <li>variable user data</li>
 * <li>sender ID</li>
 * <li>status</li>
 * </ul>
 * The optional data contains the following parts:
 * <ul>
 * <li>sub telegram number</li>
 * <li>destination ID</li>
 * <li>RSSI value (<code>0xFF</code> when sending a packet)</li>
 * <li>security level</li>
 * </ul>
 * 
 * @author Andreas Tennert
 */
public class RadioPacket extends Packet {

    /**
     * General constructor.
     * 
     * @param data the required data part
     * @param optional optional data
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public RadioPacket( int[] data, int[] optional, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_RADIO, data, optional, timestamp, isValid );
    }

    /**
     * General constructor without optional data.
     * 
     * @param data the required data part
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public RadioPacket( int[] data, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_RADIO, data, new int[0], timestamp, isValid );
    }

    /**
     * Constructor with separated required data: R-ORG, user data, senderID and
     * status without the optional data part.
     * 
     * @param rOrg the R-ORG value
     * @param userData the user data
     * @param senderID the sender ID
     * @param status the status
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public RadioPacket( int rOrg, int[] userData, int[] senderID, int status, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_RADIO, new int[6 + userData.length], new int[0], timestamp, isValid );

        this.data[0] = rOrg;
        System.arraycopy( userData, 0, this.data, 1, userData.length );
        System.arraycopy( senderID, 0, data, userData.length, senderID.length );
        this.data[userData.length + senderID.length] = status;
    }

    /**
     * Constructor with separated required data: R-ORG, user data, senderID and
     * status with the optional data part.
     * 
     * @param rOrg the R-ORG value
     * @param payload the user data
     * @param senderID the sender ID
     * @param status the status
     * @param optional the optional data part
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public RadioPacket( int rOrg, int[] payload, int[] senderID, int status, int[] optional, Date timestamp,
            boolean isValid ) {
        super( IPacketConstants.TYPE_RADIO, new int[6 + payload.length], optional, timestamp, isValid );

        this.data[0] = rOrg;
        System.arraycopy( payload, 0, this.data, 1, payload.length );
        System.arraycopy( senderID, 0, data, payload.length, senderID.length );
        this.data[payload.length + senderID.length] = status;
    }

    /**
     * Constructor with separated required and optional data: R-ORG, user data,
     * senderID, status, sub telegram number, destination ID, RSSI value and
     * security level.
     * 
     * @param rOrg the R-ORG value
     * @param payload the user data
     * @param senderID the sender ID
     * @param status the status
     * @param subTelNum the sub telegram number
     * @param destinationID the destination ID
     * @param dBm the RSSI value
     * @param securityLevel the security level
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public RadioPacket( int rOrg, int[] payload, int[] senderID, int status, int subTelNum, int[] destinationID,
            int dBm, int securityLevel, Date timestamp, boolean isValid ) {
        super( IPacketConstants.TYPE_RADIO, new int[6 + payload.length], new int[7], timestamp, isValid );

        this.data[0] = rOrg;
        System.arraycopy( payload, 0, this.data, 1, payload.length );
        System.arraycopy( senderID, 0, data, payload.length, senderID.length );
        this.data[payload.length + senderID.length] = status;

        this.optional[0] = subTelNum;
        System.arraycopy( destinationID, 0, this.optional, 1, destinationID.length );
        this.optional[5] = dBm;
        this.optional[6] = securityLevel;
    }

    /**
     * Constructor with required data part and separated optional data: sub
     * telegram number, destination ID, RSSI value and security level.
     * 
     * @param data the required data part
     * @param subTelNum the sub telegram number
     * @param destinationID the destination ID
     * @param dBm the RSSI value
     * @param securityLevel the security level
     * @param timestamp time of reception or instance creation
     * @param isValid is the data correct
     */
    public RadioPacket( int[] data, int subTelNum, int[] destinationID, int dBm, int securityLevel, Date timestamp,
            boolean isValid ) {
        super( IPacketConstants.TYPE_RADIO, data, new int[7], timestamp, isValid );

        this.optional[0] = subTelNum;
        System.arraycopy( destinationID, 0, this.optional, 1, destinationID.length );
        this.optional[5] = dBm;
        this.optional[6] = securityLevel;
    }

    /**
     * @return the sub telegram number
     */
    public int getSubTelNum() {
        if( optional.length > 0 ) {
            return optional[0];
        }
        else {
            return -1;
        }
    }

    /**
     * @return the destination ID
     */
    public int[] getDestinationID() {
        if( optional.length > 0 ) {
            final int[] destinationID = new int[4];
            System.arraycopy( optional, 1, destinationID, 0, 4 );
            return destinationID;
        }
        else {
            return new int[0];
        }
    }

    /**
     * @return the RSSI value
     */
    public int getDBm() {
        if( optional.length > 0 ) {
            return optional[5];
        }
        else {
            return -1;
        }
    }

    /**
     * @return the security level
     */
    public int getSecurityLevel() {
        if( optional.length > 0 ) {
            return optional[6];
        }
        else {
            return -1;
        }
    }
}
