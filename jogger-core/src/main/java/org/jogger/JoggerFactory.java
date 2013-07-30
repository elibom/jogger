package org.jogger;

/**
 * Implementations of this interface are used by the {@link JoggerServer} to create an instance of the {@link Jogger}
 * class. It exists to
 *
 * @author German Escobar
 */
public interface JoggerFactory {

	/**
	 * This method is called by the {@link JoggerServer} to create an instance of the {@link Jogger} class. Generally,
	 * this method should always return a new instance of the {@link Jogger} class.
	 *
	 * @return a configured {@link Jogger} instance.
	 * @throws Exception
	 */
	Jogger configure() throws Exception;

}
