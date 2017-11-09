# Code-Konventionen

### Die Mitwirkenden ...

1. Beschreiben Funktionen in mindesten einem Satz in einem Kommentar am Anfang der Funktion.
2. Benennen Variablen bezeichnent und nutzen CamelCase wenn möglich, ansonsten werden Unterstriche ( _ ) genutzt um Worte zu trennen.
3. Schreiben *keine* über ~80 Zeichen langen Codezeilen, sondern splitten diese so weit es möglich in kürze und verständliche Zeilen.

### Vorschlag
1. verweden das Prinzip von **Clean Code**. Daher versuchen sie Kommentare für Klassen, Funktionen, Eigenschaften und Variablen zu vermeiden indem sie aussagekräftige Namen verwenden. 

Beispiel für java:

Interface:     public interface GpsTracker { ... }

Klasse:        public class GpsTrackerImplementation implements GpsTracker { ... }

Methode:       public GpsPosition getGpsPosition()

Eigenschaften: private List<GpsPosition> gpsPositionHistory;
               public List<GpsPosition> getGpsPositionHistory() {
                   return gpsPositionHistory;
               }
               public void setGpsPositionHistory(List<GpsPosition> gpsPositionHistory) {
                   this.gpsPositionHistory = gpsPositionHistory;
               } 
  
Variablen: GpsTracker gpsTracker = GpsTrackerFactory.getGpsTracker();

2. verwenden prägnante Kommentare um komplexe Sachverhalte innerhalb einer Methode zu verdeutlichen.
3. verwenden die für die jeweilige Programmiersprache übliche Schreibweise von Bezeichnern also camelCase oder PascalCase.
4. bleiben dem **KISS-Prinzip** treu. (https://de.wikipedia.org/wiki/KISS-Prinzip) Daher schreiben sie keine Methoden die mehr als 40 Zeilen benötigen.
5. bleiben dem **DRY-Prinzip** treu. (https://de.wikipedia.org/wiki/Don%E2%80%99t_repeat_yourself) Daher werden sie copy&paste mit bedacht einsetzen.
