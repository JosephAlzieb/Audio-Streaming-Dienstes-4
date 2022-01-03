pragma auto_vacuum = 1;
pragma encoding = 'UTF-8';
pragma foreign_keys = 1;
pragma journal_mode = WAL;
pragma synchronous = NORMAL;


select Email, Kuenstlername, count(*) as anzahlTitel from
    ( SELECT k.*, count (TitelID) as anzahl
      from Kuenstler k JOIN Kuenstler_veroeffentlicht_Titel kvt
                            on k.Email = kvt.Email
      GROUP by TitelID
      having anzahl = 1)
group by Email
having anzahlTitel = 3;


SELECT ID, avg_Bewertung
from (
         SELECT p.*,avg(nbp.Bewertung)as avg_Bewertung
         from Playlist p JOIN Nutzer_bewertet_PlayList nbp
                              on p.ID = nbp.PlaylistID
         GROUP by p.ID
     )
where avg_Bewertung >= (
    SELECT max(avg_Bewertung)
    from (SELECT p.*,avg(nbp.Bewertung)as avg_Bewertung
          from Playlist p JOIN Nutzer_bewertet_PlayList nbp
                               on p.ID = nbp.PlaylistID
          GROUP by p.ID)
);


SELECT n.* from Nutzer n
WHERE n.Email not in
      (SELECT Email from Kuenstler_veroeffentlicht_Album)
ORDER by n.Benutzername ASC;
