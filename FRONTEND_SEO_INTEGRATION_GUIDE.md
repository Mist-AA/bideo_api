# 🚀 Frontend Integration Guide for Video SEO & Metadata

This guide provides instructions and copy-pasteable TypeScript/React code snippets to integrate the new backend SEO metadata, slug-based routing, video upload/edit features, and search in the frontend application (`bideo_ui`).

---

## 1. Domain & API Endpoints

- **Production Domain**: `https://bideo.rajit.cc`
- **Backend API Base**: `https://bideo.rajit.cc/api` (or `http://localhost:8085/api` during local development)

### Available API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/vid/slug/{slug}` | **Primary Video Lookup** (returns video + precomputed JSON-LD & meta tags) |
| `GET` | `/api/vid/view/{videoId}` | Lookup by numeric ID (increments views) |
| `POST` | `/api/vid/upload` | Multipart upload with optional `description`, `tags`, `category`, `visibility` |
| `PUT` | `/api/vid/{videoId}?userId={userId}` | Edit video title, description, tags, category, visibility |
| `GET` | `/api/vid/search?query=&category=&tag=&page=&size=` | Search & filter public completed videos |
| `GET` | `/api/vid/all?page=&size=` | Public completed videos feed |

---

## 2. TypeScript Interfaces

Add or update your video type definitions in `src/types/video.ts` (or `global.ts`):

```typescript
export type VideoCategory =
  | 'TECH'
  | 'EDUCATION'
  | 'GAMING'
  | 'ENTERTAINMENT'
  | 'MUSIC'
  | 'NEWS'
  | 'SPORTS'
  | 'LIFESTYLE'
  | 'OTHER';

export type VideoVisibility = 'PUBLIC' | 'UNLISTED' | 'PRIVATE';

export type VideoStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';

export interface SeoMetadataDTO {
  title: string;
  description: string;
  canonicalUrl: string;
  jsonLdVideoObject: Record<string, any>;
  openGraph: Record<string, string>;
  twitterCard: Record<string, string>;
}

export interface UserDTO {
  userId: string;
  user_name: string;
  user_email?: string;
  user_role?: string;
  avatar_url?: string;
}

export interface VideoDTO {
  videoId: number;
  video_title: string;
  slug: string;
  description?: string;
  tags?: string[];
  category?: VideoCategory;
  visibility?: VideoVisibility;
  video_uploadDate: string;
  video_views: number;
  m3u8Url?: string;
  video_duration?: string;
  iso_duration?: string;
  thumbnail_url?: string;
  videoStatus: VideoStatus;
  video_uploader?: UserDTO;
  seo?: SeoMetadataDTO;
}

export interface VideoMetadataUpdateRequest {
  videoTitle?: string;
  description?: string;
  tags?: string[];
  category?: VideoCategory;
  visibility?: VideoVisibility;
}
```

---

## 3. SEO Head & JSON-LD Component (`VideoSeoHead.tsx`)

Whether using **React Helmet**, **Next.js (`<Head>`)**, or native DOM manipulation, use the precomputed `video.seo` object:

```tsx
import React, { useEffect } from 'react';
import { VideoDTO } from '../types/video';

interface VideoSeoHeadProps {
  video: VideoDTO;
}

export const VideoSeoHead: React.FC<VideoSeoHeadProps> = ({ video }) => {
  const seo = video.seo;

  useEffect(() => {
    if (!seo) return;

    // Document title
    document.title = `${seo.title} | bideo.rajit.cc`;

    // Helper to set or create meta tags
    const setMetaTag = (property: string, content: string, isName = false) => {
      const selector = isName ? `meta[name="${property}"]` : `meta[property="${property}"]`;
      let element = document.querySelector(selector);
      if (!element) {
        element = document.createElement('meta');
        if (isName) element.setAttribute('name', property);
        else element.setAttribute('property', property);
        document.head.appendChild(element);
      }
      element.setAttribute('content', content);
    };

    // Standard meta
    setMetaTag('description', seo.description, true);

    // OpenGraph
    if (seo.openGraph) {
      Object.entries(seo.openGraph).forEach(([key, value]) => {
        setMetaTag(key, value);
      });
    }

    // Twitter Cards
    if (seo.twitterCard) {
      Object.entries(seo.twitterCard).forEach(([key, value]) => {
        setMetaTag(key, value, true);
      });
    }

    // Canonical link
    let canonical = document.querySelector('link[rel="canonical"]');
    if (!canonical) {
      canonical = document.createElement('link');
      canonical.setAttribute('rel', 'canonical');
      document.head.appendChild(canonical);
    }
    canonical.setAttribute('href', seo.canonicalUrl);

    // Schema.org JSON-LD VideoObject script tag
    const scriptId = 'video-schema-jsonld';
    let scriptTag = document.getElementById(scriptId) as HTMLScriptElement;
    if (!scriptTag) {
      scriptTag = document.createElement('script');
      scriptTag.id = scriptId;
      scriptTag.type = 'application/ld+json';
      document.head.appendChild(scriptTag);
    }
    scriptTag.text = JSON.stringify(seo.jsonLdVideoObject);

    return () => {
      // Optional cleanup on unmount
      const existingScript = document.getElementById(scriptId);
      if (existingScript) existingScript.remove();
    };
  }, [seo]);

  return null;
};
```

---

## 4. Slug-Based Video Watch Page (`WatchPage.tsx`)

Update your router (e.g. React Router) to route `/watch/:slug`:

```tsx
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { VideoDTO } from '../types/video';
import { VideoSeoHead } from '../components/VideoSeoHead';

export const WatchPage: React.FC = () => {
  const { slug } = useParams<{ slug: string }>();
  const [video, setVideo] = useState<VideoDTO | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!slug) return;
    setLoading(true);

    // Fetches by slug directly
    axios.get<VideoDTO>(`/api/vid/slug/${slug}`)
      .then(res => setVideo(res.data))
      .catch(err => {
        console.error("Failed to load video", err);
      })
      .finally(() => setLoading(false));
  }, [slug]);

  if (loading) return <div>Loading video...</div>;
  if (!video) return <div>Video not found</div>;

  return (
    <div className="watch-container">
      {/* Dynamic SEO Meta Tags & Schema.org JSON-LD */}
      <VideoSeoHead video={video} />

      <h1>{video.video_title}</h1>
      {/* Video Player (HLS) */}
      <video controls src={video.m3u8Url} poster={video.thumbnail_url} style={{ width: '100%', maxWidth: 960 }} />

      <div className="video-metadata">
        <span>{video.video_views} views</span> • <span>{new Date(video.video_uploadDate).toLocaleDateString()}</span>
        <p>{video.description}</p>
        <div className="tags">
          {video.tags?.map(tag => (
            <span key={tag} className="tag-pill">#{tag}</span>
          ))}
        </div>
      </div>
    </div>
  );
};
```

---

## 5. Upload Video with Full Metadata

Update your upload form to send `description`, `tags`, `category`, and `visibility`:

```typescript
export const uploadVideoWithMetadata = async (
  file: File,
  title: string,
  userId: string,
  description?: string,
  tags?: string[],
  category: string = 'OTHER',
  visibility: string = 'PUBLIC'
) => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('videoTitle', title);
  formData.append('userId', userId);
  if (description) formData.append('description', description);
  if (tags && tags.length > 0) {
    tags.forEach(tag => formData.append('tags', tag));
  }
  formData.append('category', category);
  formData.append('visibility', visibility);

  const response = await axios.post('/api/vid/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });

  return response.data;
};
```

---

## 6. Edit Video Metadata API

Provide an edit modal on the creator's video page:

```typescript
export const updateVideoMetadata = async (
  videoId: number,
  userId: string,
  payload: VideoMetadataUpdateRequest
) => {
  const response = await axios.put<VideoDTO>(
    `/api/vid/${videoId}?userId=${encodeURIComponent(userId)}`,
    payload
  );
  return response.data;
};
```

---

## 7. Video Search & Discovery API

To implement search and category filtering:

```typescript
export const searchVideos = async (
  query?: string,
  category?: string,
  tag?: string,
  page = 0,
  size = 10
) => {
  const params = new URLSearchParams();
  if (query) params.append('query', query);
  if (category && category !== 'ALL') params.append('category', category);
  if (tag) params.append('tag', tag);
  params.append('page', page.toString());
  params.append('size', size.toString());

  const response = await axios.get(`/api/vid/search?${params.toString()}`);
  return response.data; // Page<VideoDTO>
};
```
