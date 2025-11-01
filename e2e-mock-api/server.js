const express = require('express');
const cors = require('cors');

const app = express();
app.use(express.json());
app.use(cors());

// Mock data aligned with AlbumDto schema
const albums = [
  {
    id: 1,
    name: 'Mock Album 1',
    cover: 'https://picsum.photos/seed/album1/600/600',
    releaseDate: '2020-05-10',
    description: 'Primer álbum de prueba para E2E',
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
  // For list, return albums without heavy nested arrays if desired. Keeping complete for simplicity.
  res.json(albums.map(({ tracks, performers, comments, ...rest }) => rest));
});

app.get('/albums/:id', (req, res) => {
  const id = Number(req.params.id);
  const album = albums.find(a => a.id === id);
  if (!album) return res.status(404).json({ error: 'Album not found' });
  res.json(album);
});

const port = process.env.PORT || 3000;
app.listen(port, () => {
  console.log(`Mock API running on http://localhost:${port}`);
});


