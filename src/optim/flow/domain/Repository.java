package optim.flow.domain;

public interface Repository<T> {
    /**
     * Saves a T object into some kind of adapted media.
     * 
     * @param object The object to save.
     * 
     * @return The ID given to the saved object.
     */
    String save(T object);

    /**
     * Saves a object into some kind of adapted media, with the given ID.
     * 
     * @param object The object to save.
     * @param ID     The object's ID.
     */
    void save(T object, String ID);

    /**
     * Loads a object from some kind of adapted media.
     * 
     * @param ID The object's ID.
     * 
     * @return The T object corresponding to the ID.
     */
    T load(String ID);
}
