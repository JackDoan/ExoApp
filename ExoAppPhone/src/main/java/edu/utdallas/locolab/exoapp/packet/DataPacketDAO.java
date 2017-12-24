package edu.utdallas.locolab.exoapp.packet;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by jack on 12/23/17.
 */
@Entity
public class DataPacketDAO extends DataPacket {

    @Id
    private long id;

    DataPacketDAO() {
        super();
    }

    DataPacketDAO(byte[] in) {
        super(in);
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
