package org.apache.commons.betwixt.recursion;

/**
 * @author <a href='http://jakarta.apache.org/commons'>Jakarta Commons Team</a>, <a href='http://www.apache.org'>Apache Software Foundation</a>
 */
public class HybridBean {
    private AlienBean alienBean;
    private PersonBean personBean;

    public HybridBean(AlienBean alienBean, PersonBean personBean) {
        setAlien(alienBean);
        setPerson(personBean);
    }
    
    public HybridBean () {}
    
   
    public AlienBean getAlien() {
        return alienBean;
    }

    public void setAlien(AlienBean alienBean) {
        this.alienBean = alienBean;
    }

    public PersonBean getPerson() {
        return personBean;
    }

    public void setPerson(PersonBean personBean) {
        this.personBean = personBean;
    }
}
