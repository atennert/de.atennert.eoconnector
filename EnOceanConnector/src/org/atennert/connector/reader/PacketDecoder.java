/*******************************************************************************
 * Copyright (C) 2014 Andreas Tennert. 
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *******************************************************************************/

package org.atennert.connector.reader;

import java.util.Calendar;
import java.util.concurrent.BlockingQueue;

import org.atennert.connector.distribution.PacketDistributor;
import org.atennert.connector.packets.IPacketFactory;
import org.atennert.connector.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Takes data packet data from list, converts it into
 * {@link Packet} objects and puts it in maps.
 * 
 * Parts of a data packet:
 * <ul>
 * <li>type</li>
 * <li>time</li>
 * <li>data</li>
 * <li>optional</li>
 * <li>check</li>
 * <ul>
 * 
 * @author Andreas Tennert
 *
 */
public class PacketDecoder implements Runnable{

	//chipId += (value < 0x10 ? "0" : "");
	//chipId += Integer.toHexString(value);
	//if (status == 12){
//	    data.put("id", chipId);
//	    chipId = "";
	//}

    private static final Logger log = LoggerFactory.getLogger(PacketDecoder.class);
    
	private final BlockingQueue<Integer> messageByteQueue;
	private final PacketDistributor distributor;
	
	/** if this variable is set to false, then the thread stops */
	private volatile boolean run;
	
	private IPacketFactory factory;
	
    /**
	 * Registers the synchronized list and the evaluator.
	 * 
	 * @param messageByteQueue
	 * @param distributor
	 */
	public PacketDecoder(BlockingQueue<Integer> messageByteQueue, PacketDistributor distributor, IPacketFactory factory){
		this.messageByteQueue = messageByteQueue;
		this.distributor = distributor;
		this.factory = factory;
	}
	
    /**
     * Method that allows to terminate the thread.
     * @param run
     */
	public void stopThread(){
	    this.run = false;
	}

	/**
	 * runtime method: initializes thread and forwards
	 * data to evaluator
	 */
	@Override
	public void run() {
		run = true;
		
		log.debug("Consumer started");
		while (run) {
		    Packet packet;
			try {
				Thread.sleep(500);
			} catch(InterruptedException e) { }
			
			while ((packet = readData()) != null){
				distributor.distributePacket(packet);
			}
		}
		log.debug("Consumer stopped");
	}

	/**
	 * Reads all currently available values from the list and puts
	 * them in a map. Finishes when there are no more values or when
	 * a data set is complete.
	 * 
	 * @return packet type if data is complete otherwise -1
	 */
    private Packet readData(){
		Integer value = messageByteQueue.poll();
		
		// get synchronization byte
		boolean syncByteFound = false;
		while(value != null && !syncByteFound) {
		    if (value.intValue() == 0x55)
		        syncByteFound = true;
		    
		    value = messageByteQueue.poll();
		}
		
		// read header {data length (2x), optional length, packet type, checksum}
		int[] header = {-1,-1,-1,-1,-1};
    	for (int i=0; i<header.length; i++){
    	    if (value != null){
    	        header[i] = value;
                value = messageByteQueue.poll();
    		} else {
    		    return null;
    		}
		}
    	
    	// check header
	    int checksum = 0;
	    for (int i=0; i<header.length-1; i++)
	        checksum = CodingHelper.processCRC8(checksum, header[i]);
	    if ((checksum & 0xFF) != header[4]){
	        return null;
	    }
		    
    	// get data
    	int[] data = new int[(header[0] << 2) + header[1]];
    	for (int i=0; i<data.length; i++){
            if (value != null){
                data[i] = value;
                value = messageByteQueue.poll();
            } else {
                return null;
            }
    	}
    	
    	// get optional
        int[] optional = new int[header[2]];
        for (int i=0; i<optional.length; i++){
            if (value != null){
                optional[i] = value;
                value = messageByteQueue.poll();
            } else {
                return null;
            }
        }
        
        if (value == null){
            return null;
        }
        
        // check payload
        checksum = CodingHelper.calculatePayloadChecksum(data, optional);
        boolean dataValid = (checksum & 0xFF) == value;
        
        // put data in packet
        Packet packet = factory.createPacket(header[3], data, optional, Calendar.getInstance().getTime(), dataValid);

        // return packet type
		return packet;
	}
}
