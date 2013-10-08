package org.osiam.storage.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: wallner
 * Date: 08/10/13
 * Time: 11:21
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class ExtensionEntity {

    @Id
    private long id;
    private String uri;
    private UserEntity owner;


    private Set<KeyValueAttribute> attributeSet;
}
