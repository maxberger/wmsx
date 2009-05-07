/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.uibk.dps.wmsxgui.business;

/**
 *
 * @author bafu
 */
public class BusinessManager {

    private Requestor requestor;

    /* Singleton Pattern */
	private BusinessManager()
	{
        requestor = Requestor.getInstance();
    }

    /** Private innere statische Klasse, realisiert Singleton Pattern
	 *
	 */
	private static class SingletonHolder
	{
		private static BusinessManager INSTANCE = new BusinessManager();
	}

	/** Gibt immer die gleiche Instanz zurück
	 * @return Instance der Game Klasse
	 */
	public static BusinessManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	/* Singleton */

    public Requestor getRequestor()
    {
        return requestor;
    }
}
