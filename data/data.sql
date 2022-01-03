pragma auto_vacuum = 1;
pragma encoding = "UTF-8";
pragma foreign_keys = 1;
pragma journal_mode = WAL;
pragma synchronous = NORMAL;

INSERT INTO "Adresse" ("AdresseID","Land","Stadt","PLZ","Strasse","HausNr")
VALUES (1,'Deutschland','Duessldorf','11223','Hauptstr.',11);
INSERT INTO "Adresse" ("AdresseID","Land","Stadt","PLZ","Strasse","HausNr")
VALUES (2,'Deutschland','Duessldorf','11223','Hauptstr.',12);
INSERT INTO "Adresse" ("AdresseID","Land","Stadt","PLZ","Strasse","HausNr")
VALUES (3,'Deutschland','Duessldorf','11223','Hauptstr.',13);
INSERT INTO "Adresse" ("AdresseID","Land","Stadt","PLZ","Strasse","HausNr")
VALUES (4,'Deutschland','Duessldorf','11223','Hauptstr.',13);
INSERT INTO "Adresse" ("AdresseID","Land","Stadt","PLZ","Strasse","HausNr")
VALUES (5,'Deutschland','Duessldorf','11223','Hauptstr.',13);


INSERT INTO "Nutzer" ("Email","Benutzername","Passwort","AdresseID") 
VALUES ('joe@web.de','joe','00AB',1);
INSERT INTO "Nutzer" ("Email","Benutzername","Passwort","AdresseID")
VALUES ('joe2@web.de','joe2','00AB',2);
INSERT INTO "Nutzer" ("Email","Benutzername","Passwort","AdresseID")
VALUES ('joe3@web.de','joe3','00AB',3);
INSERT INTO "Nutzer" ("Email","Benutzername","Passwort","AdresseID")
VALUES ('joe4@web.de','joe4','00AB',3);
INSERT INTO "Nutzer"  ("Email","Benutzername","Passwort","AdresseID")
VALUES ('joe5@web.de','joe5','00AB',5);
INSERT INTO "Nutzer" ("Email","Benutzername","Passwort","AdresseID") 
VALUES ('joe6@web.de','joe6','00AB',5);


INSERT INTO "PremiumNutzer" ("Email","MitgliedschaftsDtaum") VALUES ('Joe2@web.de','2021-11-14');
INSERT INTO "PremiumNutzer" ("Email","MitgliedschaftsDtaum") VALUES ('Joe3@web.de','2021-11-14');
INSERT INTO "PremiumNutzer" ("Email","MitgliedschaftsDtaum") VALUES ('Joe4@web.de','2021-11-14');
INSERT INTO "PremiumNutzer" ("Email","MitgliedschaftsDtaum") VALUES ('Joe5@web.de','2021-11-14');


INSERT INTO "Kuenstler" ("Email","Kuenstlername") VALUES ('joe2@web.de','joe2');
INSERT INTO "Kuenstler" ("Email","Kuenstlername") VALUES ('joe3@web.de','joe3');
INSERT INTO "Kuenstler" ("Email","Kuenstlername") VALUES ('joe4@web.de','joe4');
INSERT INTO "Kuenstler" ("Email","Kuenstlername") VALUES ('joe5@web.de','joe5');



INSERT INTO "Band" ("BandID", "Name") VALUES (1,'Band1');
INSERT INTO "Band" ("BandID", "Name") VALUES (2,'Band2');

INSERT INTO "Geschichte" ("GeschichteID","Text","BandID") VALUES (1,'Hier ein text',1);
INSERT INTO "Geschichte" ("GeschichteID","Text","BandID") VALUES (2,'Hier ein text',2);


INSERT INTO "Speicherort" (ID, LQ, HQ) VALUES (1,'lq1','hq1');
INSERT INTO "Speicherort" (ID, LQ, HQ) VALUES (2,'lq2','hq2');
INSERT INTO "Speicherort" (ID, LQ, HQ) VALUES (3,'lq3','hq3');
INSERT INTO "Speicherort" (ID, LQ, HQ) VALUES (4,'lq4','hq4');
INSERT INTO "Speicherort" (ID, LQ, HQ) VALUES (5,'lq5','hq5');


INSERT INTO "Album" ("AlbumID","Bezeichnung","Erscheinungsjahr") VALUES (1,'album1',2019);
INSERT INTO "Album" ("AlbumID","Bezeichnung","Erscheinungsjahr") VALUES (2,'album2',2019);
INSERT INTO "Album" ("AlbumID","Bezeichnung","Erscheinungsjahr") VALUES (3,'album3',2019);
INSERT INTO "Album" ("AlbumID","Bezeichnung","Erscheinungsjahr") VALUES (4,'album4',2021);


INSERT INTO "Genre" ("Bezeichnung") VALUES ('genreI');
INSERT INTO "Genre" ("Bezeichnung") VALUES ('genreII');
INSERT INTO "Genre" ("Bezeichnung") VALUES ('genreIII');
INSERT INTO "Genre" ("Bezeichnung") VALUES ('GG');


INSERT INTO "Titel" ("ID","Benennung","Dauer","GenreBezeichnung","SpeicherortID")
VALUES (1,'titel11',30,'genreI',1);
INSERT INTO "Titel" ("ID","Benennung","Dauer","GenreBezeichnung","SpeicherortID") 
VALUES (2,'titel1',30,'genreI',2);
INSERT INTO "Titel" ("ID","Benennung","Dauer","GenreBezeichnung","SpeicherortID") 
VALUES (3,'tite31',30,'genreII',3);
INSERT INTO "Titel" ("ID","Benennung","Dauer","GenreBezeichnung","SpeicherortID")
VALUES (5,'titel5',50,'genreII',5);
INSERT INTO "Titel" ("ID","Benennung","Dauer","GenreBezeichnung","SpeicherortID")
VALUES (4,'titel4',35,'genreI',4);


INSERT INTO "Playlist" ("ID","Bezeichnung","Coverbild","oeffentlich","Email")
VALUES (1,'p1','p1.png',0,'joe2@web.de');
INSERT INTO "Playlist" ("ID","Bezeichnung","Coverbild","oeffentlich","Email")
VALUES (2,'p2',NULL,1,'joe2@web.de');
INSERT INTO "Playlist" ("ID","Bezeichnung","Coverbild","oeffentlich","Email")
VALUES (3,'p3','p3.png',1,'joe3@web.de');
INSERT INTO "Playlist" ("ID","Bezeichnung","Coverbild","oeffentlich","Email")
VALUES (4,'p4','p4.png',1,'joe3@web.de');
INSERT INTO "Playlist" ("ID","Bezeichnung","Coverbild","oeffentlich","Email")
VALUES (5,'p5','p5.png',1,'joe3@web.de');


INSERT INTO "Band_besteht_aus_Kuenstler" ("Email", "BandID") VALUES ('joe2@web.de',1);
INSERT INTO "Band_besteht_aus_Kuenstler" ("Email", "BandID") VALUES ('joe3@web.de',1);
INSERT INTO "Band_besteht_aus_Kuenstler" ("Email", "BandID") VALUES ('joe4@web.de',2);
INSERT INTO "Band_besteht_aus_Kuenstler" ("Email", "BandID") VALUES ('joe5@web.de',2);


INSERT INTO "Kuenstler_veroeffentlicht_Titel" ("Email", "TitelID") VALUES ('joe2@web.de',1);
INSERT INTO "Kuenstler_veroeffentlicht_Titel" ("Email", "TitelID") VALUES ('joe3@web.de',1);
INSERT INTO "Kuenstler_veroeffentlicht_Titel" ("Email", "TitelID") VALUES ('joe4@web.de',2);
INSERT INTO "Kuenstler_veroeffentlicht_Titel" ("Email", "TitelID") VALUES ('joe5@web.de',3);
INSERT INTO "Kuenstler_veroeffentlicht_Titel" ("Email", "TitelID") VALUES ('joe5@web.de',4);
INSERT INTO "Kuenstler_veroeffentlicht_Titel" ("Email", "TitelID") VALUES ('joe5@web.de',5);


INSERT INTO "Band_veroeffentlicht_Album" ("AlbumID","BandID") VALUES (1,1);
INSERT INTO "Band_veroeffentlicht_Album" ("AlbumID","BandID") VALUES (2,1);

INSERT INTO "Kuenstler_veroeffentlicht_Album" ("AlbumID","Email") VALUES (3,'joe2@web.de');
INSERT INTO "Kuenstler_veroeffentlicht_Album" ("AlbumID","Email") VALUES (4,'joe3@web.de');


INSERT INTO "Genre_empfiehlt_Genre" ("Bezeichnung1", "Bezeichnung2") VALUES ('GG','genreI');
INSERT INTO "Genre_empfiehlt_Genre" ("Bezeichnung1", "Bezeichnung2") VALUES ('genreII','genreI');
INSERT INTO "Genre_empfiehlt_Genre" ("Bezeichnung1", "Bezeichnung2") VALUES ('genreIII','genreII');

INSERT INTO "Titel_gehoert_zu_Album" ("TitelID","AlbumID") VALUES (1,1);
INSERT INTO "Titel_gehoert_zu_Album" ("TitelID","AlbumID") VALUES (2,1);
INSERT INTO "Titel_gehoert_zu_Album" ("TitelID","AlbumID") VALUES (3,2);

INSERT INTO "Titel_gehoert_zu_Playlist" ("TitelID","PlaylistID") VALUES (1,1);
INSERT INTO "Titel_gehoert_zu_Playlist" ("TitelID","PlaylistID") VALUES (2,1);
INSERT INTO "Titel_gehoert_zu_Playlist" ("TitelID","PlaylistID") VALUES (3,2);
INSERT INTO "Titel_gehoert_zu_Playlist" ("TitelID","PlaylistID") VALUES (3,4);
INSERT INTO "Titel_gehoert_zu_Playlist" ("TitelID","PlaylistID") VALUES (3,3);
INSERT INTO "Titel_gehoert_zu_Playlist" ("TitelID","PlaylistID") VALUES (2,3);
INSERT INTO "Titel_gehoert_zu_Playlist" ("TitelID","PlaylistID") VALUES (1,3);
INSERT INTO "Titel_gehoert_zu_Playlist" ("TitelID","PlaylistID") VALUES (3,5);


INSERT INTO "Nutzer_kommentiert_Titel" ("Email","TitelID","Text") VALUES ('joe@web.de',1,'kommentar');
INSERT INTO "Nutzer_kommentiert_Titel" ("Email","TitelID","Text") VALUES ('joe@web.de',2,'kommentar');
INSERT INTO "Nutzer_kommentiert_Titel" ("Email","TitelID","Text") VALUES ('joe3@web.de',3,'kommentar2');
INSERT INTO "Nutzer_kommentiert_Titel" ("Email","TitelID","Text") VALUES ('joe@web.de',3,'kommentar');
INSERT INTO "Nutzer_kommentiert_Titel" ("Email","TitelID","Text") VALUES ('joe2@web.de',4,'kommentar');
INSERT INTO "Nutzer_kommentiert_Titel" ("Email","TitelID","Text") VALUES ('joe@web.de',4,'kommentar');
INSERT INTO "Nutzer_kommentiert_Titel" ("Email","TitelID","Text") VALUES ('joe4@web.de',3,'kommentar');


INSERT INTO "Nutzer_bewertet_PlayList" ("Email","PlaylistID","Bewertung") VALUES ('joe@web.de',2,2);
INSERT INTO "Nutzer_bewertet_PlayList" ("Email","PlaylistID","Bewertung") VALUES ('joe3@web.de',2,2);
INSERT INTO "Nutzer_bewertet_PlayList" ("Email","PlaylistID","Bewertung") VALUES ('joe3@web.de',3,5);
INSERT INTO "Nutzer_bewertet_PlayList" ("Email","PlaylistID","Bewertung") VALUES ('joe4@web.de',3,5);
INSERT INTO "Nutzer_bewertet_PlayList" ("Email","PlaylistID","Bewertung") VALUES ('joe4@web.de',2,7);
INSERT INTO "Nutzer_bewertet_PlayList" ("Email","PlaylistID","Bewertung") VALUES ('joe@web.de',3,4);
