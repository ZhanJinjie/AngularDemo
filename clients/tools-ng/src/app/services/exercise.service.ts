import { Injectable } from '@angular/core';
import { HttpService } from './http.service';
import { Exercise } from "../models/exercise";
import QueryOptions from "../queries/query-options";
import { Observable } from "rxjs";
import { Page } from "../queries/page";
@Injectable({
  providedIn: 'root'
})
export class ExerciseService extends HttpService<Exercise> {
  constructor() {
    super();
    this.requestBase = "exercises";
  }

  saveSimilar(id: string, targetId: string, similar: number) {
    return this.putRequest(this.url(this.requestBase + "/" + id + "/similar"), {
      targetId: targetId,
      similar: similar
    });
  }
  listSimilar(id?: string, options?: QueryOptions): Observable<Page<{ exercise: Exercise; similar: number }>> {
    return this.getRequest(this.url(this.requestBase + "/" + id + "/similar", options));
  }
}
