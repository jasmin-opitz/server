package org.osiam.storage.entities;

import javax.persistence.Embeddable;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: wallner
 * Date: 08/10/13
 * Time: 11:16
 * To change this template use File | Settings | File Templates.
 */
@Embeddable
public class KeyValueAttribute {
    @Id
    private long id;
    String key;
    String val;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return val;
    }

    public void setValue(String val) {
        this.val = val;
    }
}
