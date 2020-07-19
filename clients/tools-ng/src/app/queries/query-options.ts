import Filter from './Filter';
import Sort from './Sort';

export default interface QueryOptions {
    page: number; size: number;
    filters?: Filter[];
    sorts?: Sort[];
}