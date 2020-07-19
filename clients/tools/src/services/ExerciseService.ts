import HttpService from './HttpService';
import { Exercise } from '@/models/Exercise';
import { QueryOptions } from '@/queries/QueryOptions';
import { Observable } from 'rxjs';
import { Page } from '@/queries/Page';

export default class ExerciseService extends HttpService<Exercise> {
    saveSimilar(id: string, targetId: string, similar: number) {
        return this.putRequest(this.url(this.requestBase + "/" + id + "/similar"), {
            targetId: targetId,
            similar: similar
        });
    }
    listSimilar(id?: string, options?: QueryOptions): Observable<Page<{ exercise: Exercise; similar: number }>> {
        return this.getRequest(this.url(this.requestBase + "/" + id + "/similar", options));
    }
    protected constructor() {
        super();
        this.requestBase = "exercises"
    }

    public static instance = new ExerciseService();

}