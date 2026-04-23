export interface RagSearchMatch {
  id: string;
  name: string;
  category: string;
  breed: string;
  status: string;
  description: string;
  relevance_score: number;
}

export interface RagSearchResponse {
  query: string;
  answer: string;
  matched_animals: RagSearchMatch[];
  total_matches: number;
}
