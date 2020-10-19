package optim.flow.domain;

public interface NetworkRepo {
    /**
     * Saves a network into some kind of adapted media.
     * 
     * @param network The network to save.
     * 
     * @return The ID given to the saved network, null if there's an error.
     */
    String save(Network network);

    /**
     * Saves a network into some kind of adapted media, with the given ID.
     * 
     * @param network The network to save.
     * @param ID      The network's ID.
     */
    void save(Network network, String ID);

    /**
     * Loads a network from some kind of adapted media.
     * 
     * @param ID The network's ID.
     * 
     * @return The Network object corresponding to the ID, null if there's an error.
     */
    Network load(String ID);
}
