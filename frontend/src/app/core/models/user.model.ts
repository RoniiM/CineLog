export interface User {
  id: number;
  username: string;
  email: string;
  bio: string | null;
  avatarUrl: string | null;
  createdAt: string;
}

export interface UpdateProfileRequest {
  bio: string | null;
  avatarUrl: string | null;
}
