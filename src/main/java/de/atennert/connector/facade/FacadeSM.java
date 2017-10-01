package de.atennert.connector.facade;

/**
 * This is the state machine implementation for the facade. It contains an
 * interface for transitions and an enumeration, that holds the states. The
 * {@link ConnectorFacade} controls the execution of transitions in which
 * actions can be performed new states can be set.
 *
 * @author Andreas Tennert
 */
final class FacadeSM {

    /**
     * As general transition interface this contains a method to handle a
     * transaction. <br>
     * <br>
     */
    private interface Transition {

        /**
         * This handles a transaction. It gets an instance of
         * {@link AbstractTransitionModel}, which holds resources to work with.
         * The instance type also defines what actions are to execute and what
         * state follows. The model is also used to set a new state if
         * necessary.
         *
         * @param model the model which holds the transition resources
         * @return <code>true</code> if the transition was handled, i.e. there
         * is a transition that uses the given model, <code>false</code>
         * otherwise.
         */
        boolean handle( AbstractTransitionModel model );
    }

    /**
     * This enumeration contains the states of the state machine. Each state
     * handles its following transitions.
     */
    enum State implements Transition {
        /**
         * This is the starting point of the state machine.
         */
        ENTRY {
            @Override
            public boolean handle( AbstractTransitionModel model ) {
                if (!(model instanceof InitializeModel)) {
                    return false;
                }
                model.setState( INITIALIZED );
                return true;
            }
        },

        /**
         * In this state all resources of the EOC are available but the data
         * acquisition is not running. Listeners and factories can be added or
         * removed and settings can be adjusted. Furthermore the data
         * acquisition can started.
         */
        INITIALIZED {
            private String port = null;

            @Override
            public boolean handle( AbstractTransitionModel model ) {

                if (model instanceof AcquisitionModel && ((AcquisitionModel) model).action == AcquisitionModel.START) {
                    /*
                     * re-check if port is still valid
                     */
                    if (((AcquisitionModel) model).connector.setSerialPort( port )) {
                        // start the data acquisition
                        new Thread( ((AcquisitionModel) model).connector ).start();
                        new Thread( ((AcquisitionModel) model).consumer ).start();
                        model.setState( RUNNING );
                    } else {
                        return false;
                    }

                } else if (model instanceof PacketListenerModel) {
                    // add/remove PacketListener
                    final PacketListenerModel plm = (PacketListenerModel) model;
                    switch (plm.action) {
                        case PacketListenerModel.ADD:
                            plm.distributor.addListener( plm.id, plm.listener );
                            break;
                        case PacketListenerModel.REMOVE:
                            plm.distributor.removeListener( plm.id );
                            break;
                        default:
                            return false;
                    }

                } else if (model instanceof PacketFactoryModel) {
                    // add/remove IPacketFactory
                    final PacketFactoryModel pfm = (PacketFactoryModel) model;
                    switch (pfm.action) {
                        case PacketFactoryModel.ADD:
                            pfm.mainFactory.addFactory( pfm.factory );
                            break;
                        case PacketFactoryModel.REMOVE:
                            pfm.mainFactory.removeFactory( pfm.factory );
                            break;
                        default:
                            return false;
                    }

                } else if (model instanceof PortListenerModel) {
                    // add/remove PortListener
                    final PortListenerModel plm = (PortListenerModel) model;
                    switch (plm.action) {
                        case PortListenerModel.ADD:
                            plm.distributor.addListener( plm.listener );
                            break;
                        case PortListenerModel.REMOVE:
                            plm.distributor.removeListener( plm.listener );
                            break;
                        default:
                            return false;
                    }

                } else if (model instanceof ConnectionListenerModel) {
                    // add/remove PortListener
                    final ConnectionListenerModel clm = (ConnectionListenerModel) model;
                    switch (clm.action) {
                        case ConnectionListenerModel.ADD:
                            clm.distributor.addListener( clm.listener );
                            break;
                        case ConnectionListenerModel.REMOVE:
                            clm.distributor.removeListener( clm.listener );
                            break;
                        default:
                            return false;
                    }

                } else if (model instanceof SetPortModel) {
                    // set the port for data acquisition
                    if (((SetPortModel) model).connector.setSerialPort( ((SetPortModel) model).port )) {
                        port = ((SetPortModel) model).port;
                    } else {
                        return false;
                    }
                } else if (model instanceof SendMessageModel) {
                    return false;
                }

                return true;
            }
        },

        /**
         * In this state the data acquisition is running. Adding / removing of
         * listeners or factories and changing of settings is <em>NOT</em>
         * allowed. The data acquisition can be stopped.
         */
        RUNNING {
            @Override
            public boolean handle( AbstractTransitionModel model ) {
                // stop the data acquisition
                if (model instanceof AcquisitionModel && ((AcquisitionModel) model).action == AcquisitionModel.STOP) {
                    ((AcquisitionModel) model).connector.stopThread();
                    ((AcquisitionModel) model).consumer.stopThread();

                    model.setState( INITIALIZED );
                    return true;
                } else if (model instanceof SendMessageModel) {
                    ((SendMessageModel) model).sendMessageQueue.add( ((SendMessageModel) model).packet );
                    return true;
                }
                return false;
            }
        }
    }

    private FacadeSM() {
        // never create an instance of this class
    }
}
