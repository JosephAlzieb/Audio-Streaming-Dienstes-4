
CREATE TABLE IF NOT EXISTS "Adresse" (
	"AdresseID"	INTEGER NOT NULL CHECK("AdresseID" > 0),
	"Land"	varchar NOT NULL CHECK(length(trim("Land")) > 0 AND "Land" NOT GLOB '*[^ -~]*'),
	"Stadt"	varchar NOT NULL CHECK(length(trim("Stadt")) > 0 AND "Stadt" NOT GLOB '*[^ -~]*'),
	"PLZ"	varchar NOT NULL CHECK(length("PLZ") == 5 AND "PLZ" NOT GLOB '*[^ -~]*'),
	"Strasse"	varchar NOT NULL CHECK(length(trim("Strasse")) > 0 AND "Strasse" NOT GLOB '*[^ -~]*'),
	"HausNr"	INTEGER NOT NULL CHECK("HausNr" > 0),
	PRIMARY KEY("AdresseID")
);

CREATE TABLE IF NOT EXISTS "Nutzer" (
	"Email"	varchar NOT NULL CHECK("Email" LIKE '%_@%_.%_' AND substr("Email", 1, instr("Email", '@') - 1) NOT GLOB '*[^a-zA-Z0-9]*' AND substr("Email", instr("Email", '@') + 1, instr("Email", '.') - instr("Email", '@') - 1) NOT GLOB '*[^a-zA-Z0-9]*' AND substr("Email", instr("Email", '.') + 1, length("Email") - instr("Email", '.')) NOT GLOB '*[^a-zA-Z]*') COLLATE nocase,
	"Benutzername"	varchar NOT NULL CHECK(length(trim("Benutzername")) > 0 AND "Benutzername" NOT GLOB '*[^ -~]*') UNIQUE COLLATE nocase,
	"Passwort"	varchar NOT NULL CHECK(length("Passwort") > 2 AND length("Passwort") < 9 AND "Passwort" NOT GLOB '*[^ -~]*' AND "Passwort" GLOB '*[A-Z]*[A-Z]*' AND "Passwort" GLOB '*[0-9]*'),
	"AdresseID"	INTEGER NOT NULL CHECK("AdresseID" >= 0),
	FOREIGN KEY("AdresseID") REFERENCES "Adresse"("AdresseID") on update cascade on delete cascade,
	PRIMARY KEY("Email")
);


CREATE TABLE IF NOT EXISTS "PremiumNutzer" (
	"Email"	varchar NOT NULL COLLATE NOCASE,
	"MitgliedschaftsDtaum"	date NOT NULL CHECK(date("MitgliedschaftsDtaum") IS NOT null),
	FOREIGN KEY("Email") REFERENCES "Nutzer" on update cascade on delete cascade,
	PRIMARY KEY("Email")
);


CREATE TABLE IF NOT EXISTS "Kuenstler" (
	"Email"	varchar NOT NULL COLLATE NOCASE,
	"Kuenstlername"	varchar NOT NULL CHECK(length(trim("Kuenstlername")) > 0 AND "Kuenstlername" NOT GLOB '*[^ -~]*') COLLATE NOCASE,
	FOREIGN KEY("Email") REFERENCES "PremiumNutzer"("Email") on update cascade on delete cascade,
	PRIMARY KEY("Email")
);



CREATE TABLE IF NOT EXISTS "Band" (
	"BandID"	INTEGER NOT NULL CHECK("BandID" > 0),
	"Name"	varchar NOT NULL CHECK(length(trim("Name")) > 0 AND "Name" NOT GLOB '*[^ -~]*') UNIQUE COLLATE NOCASE,
	PRIMARY KEY("BandID")
);


CREATE TABLE IF NOT EXISTS "Genre" (
	"Bezeichnung"	varchar NOT NULL CHECK(length(trim("Bezeichnung")) > 0 AND "Bezeichnung" NOT GLOB '*[^a-zA-Z]*') COLLATE NOCASE,
	PRIMARY KEY("Bezeichnung")
);


CREATE TABLE IF NOT EXISTS "Geschichte" (
	"GeschichteID"	INTEGER NOT NULL CHECK("GeschichteID" > 0),
	"Text"	TEXT NOT NULL CHECK(length(trim("Text")) > 0 AND "Text" NOT GLOB '*[^ -~]*') COLLATE NOCASE,
	"BandID"	INTEGER NOT NULL UNIQUE,
	FOREIGN KEY("BandID") REFERENCES "Band"("BandID") on update cascade on delete cascade,
	PRIMARY KEY("GeschichteID")
);

CREATE TABLE IF NOT EXISTS "Speicherort" (
	"ID"	INTEGER NOT NULL CHECK("ID" > 0),
	"LQ"	varchar NOT NULL CHECK(length(trim("LQ")) > 0 AND "LQ" NOT GLOB '*[^ -~]*') UNIQUE COLLATE NOCASE,
	"HQ"	varchar NOT NULL CHECK(length(trim("HQ")) > 0 AND "HQ" NOT GLOB '*[^ -~]*') UNIQUE COLLATE NOCASE,
	PRIMARY KEY("ID")
);


CREATE TABLE IF NOT EXISTS "Album" (
	"AlbumID"	INTEGER NOT NULL CHECK("AlbumID" > 0),
	"Bezeichnung"	varchar NOT NULL CHECK(length(trim("Bezeichnung")) > 0 AND "Bezeichnung" NOT GLOB '*[^ -~]*') COLLATE NOCASE,
	"Erscheinungsjahr"	date NOT NULL CHECK("Erscheinungsjahr" > 0 AND "Erscheinungsjahr" <= CAST(strftime('%Y', CURRENT_DATE) AS unsigned)),
	PRIMARY KEY("AlbumID"),
	UNIQUE("Bezeichnung","Erscheinungsjahr")
);


CREATE TABLE IF NOT EXISTS "Titel" (
	"ID"	INTEGER NOT NULL CHECK("ID" > 0),
	"Benennung"	varchar NOT NULL CHECK(length(trim("Benennung")) > 0 AND "Benennung" NOT GLOB '*[^ -~]*') COLLATE NOCASE,
	"Dauer"	INTEGER NOT NULL CHECK("Dauer" > 0),
	"GenreBezeichnung"	varchar NOT NULL COLLATE NOCASE,
	"SpeicherortID"	INTEGER NOT NULL,
	FOREIGN KEY("GenreBezeichnung") REFERENCES "Genre"("Bezeichnung") on update cascade on delete cascade,
	FOREIGN KEY("SpeicherortID") REFERENCES "Speicherort"("ID") on update cascade on delete cascade,
	PRIMARY KEY("ID"),
	UNIQUE("Benennung","GenreBezeichnung")
);


CREATE TABLE IF NOT EXISTS "Playlist" (
	"ID"	INTEGER NOT NULL CHECK("ID" > 0),
	"Bezeichnung"	varchar NOT NULL CHECK(length(trim("Bezeichnung")) > 0 AND "Bezeichnung" NOT GLOB '*[^ -~]*') COLLATE NOCASE,
	"Coverbild"	varchar CHECK("Coverbild" LIKE '%.png') COLLATE NOCASE,
	"oeffentlich"	boolean NOT NULL CHECK("oeffentlich" = 0 OR "oeffentlich" = 1),
	"Email"	varchar NOT NULL COLLATE NOCASE,
	FOREIGN KEY("Email") REFERENCES "PremiumNutzer"("Email") on update cascade on delete cascade,
	PRIMARY KEY("ID"),
	UNIQUE("Bezeichnung","Email")
);


CREATE TABLE IF NOT EXISTS "Band_besteht_aus_Kuenstler" (
	"Email"	varchar NOT NULL COLLATE NOCASE,
	"BandID"	INTEGER NOT NULL,
	PRIMARY KEY("Email"),
	FOREIGN KEY("BandID") REFERENCES "Band"("BandID") on update cascade on delete cascade,
	FOREIGN KEY("Email") REFERENCES "Kuenstler"("Email") on update cascade on delete cascade
);


CREATE TABLE IF NOT EXISTS "Band_veroeffentlicht_Album" (
	"AlbumID"	INTEGER NOT NULL CHECK("AlbumID" > 0),
	"BandID"	INTEGER NOT NULL CHECK("BandID" > 0),
	FOREIGN KEY("BandID") REFERENCES "Band"("BandID") on update cascade on delete cascade,
	FOREIGN KEY("AlbumID") REFERENCES "Album"("AlbumID") on update cascade on delete cascade,
	PRIMARY KEY("AlbumID")
);

CREATE TABLE IF NOT EXISTS "Kuenstler_veroeffentlicht_Album" (
	"AlbumID"	INTEGER NOT NULL CHECK("AlbumID" > 0),
	"Email"	varchar NOT NULL COLLATE NOCASE,
	FOREIGN KEY("Email") REFERENCES "Kuenstler"("Email") on update cascade on delete cascade,
	FOREIGN KEY("AlbumID") REFERENCES "Album"("AlbumID") on update cascade on delete cascade,
	PRIMARY KEY("AlbumID")
);


CREATE TABLE IF NOT EXISTS "Kuenstler_veroeffentlicht_Titel" (
	"Email"	varchar NOT NULL COLLATE NOCASE,
	"TitelID"	INTEGER NOT NULL CHECK("TitelID" > 0),
	FOREIGN KEY("Email") REFERENCES "Kuenstler"("Email") on update cascade on delete cascade,
	FOREIGN KEY("TitelID") REFERENCES "Titel"("ID") on update cascade on delete cascade,
	PRIMARY KEY("Email","TitelID")
);

CREATE TABLE IF NOT EXISTS "Titel_gehoert_zu_Album" (
	"TitelID"	INTEGER NOT NULL CHECK("TitelID" > 0),
	"AlbumID"	INTEGER NOT NULL CHECK("AlbumID" > 0),
	FOREIGN KEY("TitelID") REFERENCES "Titel"("ID") on update cascade on delete cascade,
	FOREIGN KEY("AlbumID") REFERENCES "Album"("AlbumID") on update cascade on delete cascade,
	PRIMARY KEY("TitelID","AlbumID")
);


CREATE TABLE IF NOT EXISTS "Genre_empfiehlt_Genre" (
	"Bezeichnung1"	varchar NOT NULL COLLATE NOCASE,
	"Bezeichnung2"	varchar NOT NULL CHECK("Bezeichnung1" NOT LIKE "Bezeichnung2") COLLATE NOCASE,
	PRIMARY KEY("Bezeichnung1"),
	FOREIGN KEY("Bezeichnung1") REFERENCES "Genre"("Bezeichnung") on update cascade on delete cascade,
	FOREIGN KEY("Bezeichnung2") REFERENCES "Genre"("Bezeichnung") on update cascade on delete cascade
);


CREATE TABLE IF NOT EXISTS "Titel_gehoert_zu_Playlist" (
	"TitelID"	INTEGER NOT NULL CHECK("TitelID" > 0),
	"PlaylistID"	INTEGER NOT NULL CHECK("PlaylistID" > 0),
	FOREIGN KEY("PlaylistID") REFERENCES "Playlist"("ID") on update cascade on delete cascade,
	FOREIGN KEY("TitelID") REFERENCES "Titel"("ID") on update cascade on delete cascade,
	PRIMARY KEY("TitelID","PlaylistID")
);


CREATE TABLE IF NOT EXISTS "Nutzer_kommentiert_Titel" (
	"Email"	varchar NOT NULL COLLATE NOCASE,
	"TitelID"	INTEGER NOT NULL CHECK("TitelID" > 0),
	"Text"	TEXT NOT NULL CHECK(length(trim("Text")) > 0 AND "Text" NOT GLOB '*[^ -~]*') COLLATE NOCASE,
	FOREIGN KEY("TitelID") REFERENCES "Titel"("ID") on update cascade on delete cascade,
	FOREIGN KEY("Email") REFERENCES "Nutzer"("Email") on update cascade on delete cascade,
	PRIMARY KEY("Email","TitelID")
);



CREATE TABLE IF NOT EXISTS "Nutzer_bewertet_PlayList" (
	"Email"	varchar NOT NULL COLLATE NOCASE,
	"PlaylistID"	INTEGER NOT NULL CHECK("PlaylistID" > 0),
	"Bewertung"	INTEGER NOT NULL CHECK("Bewertung" > 0 AND "Bewertung" <= 10),
	PRIMARY KEY("Email","PlaylistID"),
	FOREIGN KEY("PlaylistID") REFERENCES "Playlist"("ID") on update cascade on delete cascade,
	FOREIGN KEY("Email") REFERENCES "Nutzer"("Email") on update cascade on delete cascade
);


CREATE TRIGGER IF NOT EXISTS Titel_hat_immer_kuenstler  AFTER DELETE on Kuenstler_veroeffentlicht_Titel 
WHEN ((SELECT Email FROM Kuenstler_veroeffentlicht_Titel WHERE TitelID = old.TitelID) isNULL)
BEGIN
DELETE FROM Titel WHERE ID = old.TitelID;
END;


CREATE TRIGGER IF NOT EXISTS Playlist_hat_mindestens_ein_Titel AFTER DELETE on Titel_gehoert_zu_Playlist 
WHEN ((SELECT TitelID FROM Titel_gehoert_zu_Playlist WHERE PlaylistID = old.PlaylistID) ISNULL)
BEGIN
DELETE FROM Playlist WHERE ID = old.PlaylistID;
END;


CREATE TRIGGER IF NOT EXISTS Album_von_Kuenstler BEFORE INSERT ON Kuenstler_veroeffentlicht_Album
BEGIN
SELECT CASE 
WHEN ((SELECT AlbumID FROM Band_veroeffentlicht_Album WHERE new.AlbumID = AlbumID) NOTNULL) 
THEN RAISE(ABORT, 'Das Album wurde schon von einer Band veröffentlicht') 
END; 
END;


CREATE TRIGGER IF NOT EXISTS Album_von_Band BEFORE INSERT ON Band_veroeffentlicht_Album
BEGIN
SELECT CASE 
WHEN ((SELECT AlbumID FROM Kuenstler_veroeffentlicht_Album WHERE new.AlbumID = AlbumID) NOTNULL) 
THEN RAISE(ABORT, 'Das Album wurde schon von einem Kuenstler veroeffentlicht') 
END; 
END;


CREATE TRIGGER IF NOT EXISTS Band_hat_immer_min_zwei_kuenstlern AFTER DELETE on Band_besteht_aus_Kuenstler
WHEN ((SELECT count(Email) FROM Band_besteht_aus_Kuenstler WHERE BandID = old.BandID ) <2 )
BEGIN
DELETE FROM Band WHERE BandID = old.BandID;
END;


CREATE TRIGGER IF NOT EXISTS Album_nicht_mehr_aenderbar BEFORE UPDATE ON Album
BEGIN
SELECT CASE 
WHEN ((SELECT TitelID FROM Titel_gehoert_zu_Album WHERE old.AlbumID = AlbumID) NOTNULL) 
THEN RAISE(ABORT, 'Das Album enthält schon Titels, und kann somit nicht meht geändert werden') 
END; 
END;


CREATE TRIGGER IF NOT EXISTS Album_nicht_mehr_loeschbar BEFORE DELETE ON Album
BEGIN
SELECT CASE 
WHEN ((SELECT TitelID FROM Titel_gehoert_zu_Album WHERE old.AlbumID = AlbumID) NOTNULL) 
THEN RAISE(ABORT, 'Das Album enthält schon Titels, und kann somit nicht meht geloescht werden') 
END; 
END;


CREATE TRIGGER IF NOT EXISTS Titel_kommentieren_dann_bewerten BEFORE INSERT ON Nutzer_bewertet_PlayList
BEGIN
SELECT CASE 
WHEN ((SELECT count(*)
  FROM Nutzer_kommentiert_Titel nkt INNER JOIN Titel_gehoert_zu_Playlist tgp
  on nkt.TitelID = tgp.TitelID
  WHERE Email = new.Email and PlaylistID = new.PlaylistID) < 1) 
THEN RAISE(ABORT, 'Sie koennen die Playlist nicht bewerten, denn Sie haben noch keinen Titel aus dieser Playlist kommentiert') 
END; 
END;


CREATE TRIGGER IF NOT EXISTS nur_public_Playlist_bewerten BEFORE INSERT ON Nutzer_bewertet_PlayList
BEGIN
SELECT CASE 
WHEN (0 in (SELECT oeffentlich FROM Playlist WHERE new.PlaylistID = ID)) 
THEN RAISE(ABORT, 'dieses Playlist ist private, Sie koennen sie nicht bewerten') 
END; 
END;


CREATE TRIGGER IF NOT EXISTS nur_einmalig_kommentieren BEFORE INSERT ON Nutzer_kommentiert_Titel
BEGIN
SELECT CASE 
WHEN (EXISTS(SELECT Email FROM Nutzer_kommentiert_Titel WHERE TitelID = new.TitelID and Email = new.Email)) 
THEN RAISE(ABORT, 'Der Nutzer hat dieses Titel schon mal komentiert') 
END; 
END;
