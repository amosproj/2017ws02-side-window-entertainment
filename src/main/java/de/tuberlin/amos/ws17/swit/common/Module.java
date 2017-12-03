package de.tuberlin.amos.ws17.swit.common;

import java.awt.image.BufferedImage;

public interface Module {

    /**
     * Startet das Modul, sodass es Einsatzbereit ist. Voraussetzung ist die Initialisierung einer Instanz.
     * @throws ModuleNotWorkingException
     */
    public void startModule() throws ModuleNotWorkingException;

    /**
     * Beendet das Modul, sodass Threads geschlossen werden und die Funktionalität nichtmehr verfügbar ist.
     * @return
     */
    public boolean stopModule();

    /**
     * Falls das Modul nicht funktioniert, wird dieses Bild als Hinweis auf der Oberfläche angezeigt.
     * Bilder, die hier aufgerufen werden, gehören in "/resources/module_images/"
     * @return
     */
    public BufferedImage getModuleImage();

    /**
     * Gibt den Namen des Moduls zurück.
     * @return
     */
    public String getModuleName();


}
