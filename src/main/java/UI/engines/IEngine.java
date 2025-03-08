package UI.engines;

/**
 * Defines the contract for an engine that drives the user interface of the Transport Management System.
 * Implementations of this interface can vary depending on the type of interface required, such as
 * console-based, web-based, API-driven, or other custom engines. The engine is responsible for
 * initializing necessary components and managing the interaction flow with the user or client.
 */
public interface IEngine {

    /**
     * Starts the engine, initiating the main interaction loop or process.
     * Depending on the implementation, this could:
     * <ul>
     *     <li>For a console engine: Display a menu and handle user input in a loop.</li>
     *     <li>For a web engine: Start a server and listen for HTTP requests.</li>
     *     <li>For an API engine: Initialize endpoints and handle incoming API calls.</li>
     *     <li>For other engines: Perform custom startup logic suited to the interface type.</li>
     * </ul>
     * This method should contain the core logic for running the engine until it is stopped,
     * typically by user action (e.g., exiting a menu) or external termination (e.g., server shutdown).
     */
    public void start();
}
