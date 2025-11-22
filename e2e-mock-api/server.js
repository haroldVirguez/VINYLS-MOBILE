const express = require('express');
const cors = require('cors');

const app = express();
app.use(express.json());
app.use(cors());

// Mock data aligned with AlbumDto schema
const albums = [
  {
    id: 1,
    name: 'Buscando América Prueba',
    cover: 'https://picsum.photos/seed/album1/600/600',
    releaseDate: '2020-05-10',
    description: 'Álbum de prueba para E2E - Buscando América Prueba',
    genre: 'Rock',
    recordLabel: 'Mock Records',
    tracks: [
      { id: 101, name: 'Intro', duration: '1:23' },
      { id: 102, name: 'Single 1', duration: '3:45' }
    ],
    performers: [
      { id: 201, name: 'Mock Band', image: 'https://picsum.photos/seed/band1/400/300', description: 'Banda de prueba', birthDate: null }
    ],
    comments: [
      { id: 301, description: 'Excelente!', rating: 5 },
      { id: 302, description: 'Muy bueno', rating: 4 }
    ]
  },
  {
    id: 2,
    name: 'Mock Album 2',
    cover: 'https://picsum.photos/seed/album2/600/600',
    releaseDate: '2021-08-20',
    description: 'Segundo álbum de prueba para E2E',
    genre: 'Pop',
    recordLabel: 'Demo Label',
    tracks: [
      { id: 103, name: 'Opening', duration: '2:10' },
      { id: 104, name: 'Single 2', duration: '4:01' }
    ],
    performers: [
      { id: 202, name: 'Demo Artist', image: 'https://picsum.photos/seed/artist2/400/300', description: 'Artista de prueba', birthDate: '1990-01-01' }
    ],
    comments: []
  }
];

app.get('/albums', (req, res) => {
  res.json(albums.map(({ tracks, performers, comments, ...rest }) => rest));
});

app.get('/albums/:id', (req, res) => {
  const id = Number(req.params.id);
  const album = albums.find(a => a.id === id);
  if (!album) return res.status(404).json({ error: 'Album not found' });
  res.json(album);
});

// New endpoint: return tracks for an album
app.get('/albums/:id/tracks', (req, res) => {
  const id = Number(req.params.id);
  const album = albums.find(a => a.id === id);
  console.log(`[mock] GET /albums/${id}/tracks -> album ${album ? 'FOUND' : 'NOT FOUND'}`);
  if (!album) return res.status(404).json({ error: 'Album not found' });
  console.log(`[mock] returning ${album.tracks ? album.tracks.length : 0} tracks for album ${id}`);
  res.json(album.tracks || []);
});

app.post('/albums', (req, res) => {
  const album = req.body;
  console.log('[mock] received new album payload:', album);
  // simple id generation
  const maxId = albums.reduce((acc, a) => Math.max(acc, a.id), 0);
  const newId = maxId + 1;
  const newAlbum = Object.assign({ id: newId, tracks: [], performers: [], comments: [] }, album);
  albums.push(newAlbum);
  console.log(`[mock] created new album with id ${newId}`);
  res.status(200).json(newAlbum);
});

// New endpoint: add a track to an album (in-memory)
app.post('/albums/:id/tracks', (req, res) => {
  const id = Number(req.params.id);
  const album = albums.find(a => a.id === id);
  console.log(`[mock] POST /albums/${id}/tracks -> album ${album ? 'FOUND' : 'NOT FOUND'}`);
  if (!album) return res.status(404).json({ error: 'Album not found' });

  const track = req.body;
  console.log('[mock] received new track payload:', track);
  // simple id generation
  const maxId = albums.flatMap(a => a.tracks || []).reduce((acc, t) => Math.max(acc, t.id || 0), 0);
  const newId = maxId + 1;
  const newTrack = Object.assign({ id: newId }, track);

  album.tracks = album.tracks || [];
  album.tracks.push(newTrack);

  console.log(`[mock] album ${id} now has ${album.tracks.length} tracks`);

  res.status(201).json(newTrack);
});

// Explicit mock musicians data to be returned by /musicians
// This matches the MusicianDto shape used in the app
const musicians = [
  {
    id: 201,
    name: 'Mock Band',
    image: 'https://picsum.photos/seed/band1/400/300',
    description: 'Banda de prueba',
    birthDate: null,
    albums: [
      {
        id: 1,
        name: 'Mock Album 1',
        cover: 'https://picsum.photos/seed/album1/600/600',
        releaseDate: '2020-05-10',
        description: 'Primer álbum de prueba para E2E',
        genre: 'Rock',
        recordLabel: 'Mock Records'
      }
    ],
    performerPrizes: []
  },
  {
    id: 202,
    name: 'Demo Artist',
    image: 'https://picsum.photos/seed/artist2/400/300',
    description: 'Artista de prueba',
    birthDate: '1990-01-01',
    albums: [
      {
        id: 2,
        name: 'Mock Album 2',
        cover: 'https://picsum.photos/seed/album2/600/600',
        releaseDate: '2021-08-20',
        description: 'Segundo álbum de prueba para E2E',
        genre: 'Pop',
        recordLabel: 'Demo Label'
      }
    ],
    performerPrizes: []
  }
];

// Return all musicians
app.get('/musicians', (req, res) => {
  res.json(musicians);
});

// Return a single musician by id
app.get('/musicians/:id', (req, res) => {
  const id = Number(req.params.id);
  const m = musicians.find(x => x.id === id);
  if (!m) return res.status(404).json({ error: 'Musician not found' });
  res.json(m);
});

// Explicit mock collectors data to be returned by /collectors
const collectors = [
  {
    id: 100,
    name: 'Manolo Bellon',
    telephone: '3502457896',
    email: 'manollo@caracol.com.co',
    comments: [
      {
        id: 100,
        description: 'The most relevant album of Ruben Blades',
        rating: 5
      }
    ],
    favoritePerformers: [
      {
        id: 100,
        name: 'Rubén Blades Bellido de Luna',
        image: 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ruben_Blades_by_Gage_Skidmore.jpg/800px-Ruben_Blades_by_Gage_Skidmore.jpg',
        description: 'Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.',
        birthDate: '1948-07-16T00:00:00.000Z'
      }
    ],
    collectorAlbums: [
      {
        id: 100,
        price: 35,
        status: 'Active'
      }
    ]
  },
  {
    id: 101,
    name: 'Jaime Monsalve',
    telephone: '3012357936',
    email: 'jmonsalve@rtvc.com.co',
    comments: [
      {
        id: 101,
        description: 'I love this album of Queen',
        rating: 5
      }
    ],
    favoritePerformers: [
      {
        id: 101,
        name: 'Queen',
        image: 'https://pm1.narvii.com/6724/a8b29909071e9d08517b40c748b6689649372852v2_hq.jpg',
        description: 'Queen es una banda británica de rock formada en 1970 en Londres por el cantante Freddie Mercury, el guitarrista Brian May, el baterista Roger Taylor y el bajista John Deacon. Si bien el grupo ha presentado bajas de dos de sus miembros (Mercury, fallecido en 1991, y Deacon, retirado en 1997), los integrantes restantes, May y Taylor, continúan trabajando bajo el nombre Queen, por lo que la banda aún se considera activa.',
        creationDate: '1970-01-01T00:00:00.000Z'
      }
    ],
    collectorAlbums: [
      {
        id: 101,
        price: 25,
        status: 'Active'
      }
    ]
  }
]

// Return all collectors
app.get('/collectors', (req, res) => {
  console.log('[mock] GET /collectors -> returning', collectors.length, 'collectors')
  res.json(collectors)
})

// Return a single collector by id
app.get('/collectors/:id', (req, res) => {
  const id = Number(req.params.id)
  const c = collectors.find(x => x.id === id)
  console.log(`[mock] GET /collectors/${id} -> ${c ? 'FOUND' : 'NOT FOUND'}`)
  if (!c) return res.status(404).json({ error: 'Collector not found' })
  res.json(c)
})

const port = process.env.PORT || 3000;
app.listen(port, () => {
  console.log(`Mock API running on http://localhost:${port}`);
});
