import { Injectable } from '@angular/core';
import { HttpService } from './http.service';
import { TagDefinition } from '../models/tag-definition';
import { Observable } from 'rxjs'

@Injectable({
  providedIn: 'root'
})
export class TagDefinitionService extends HttpService<TagDefinition> {
  findByCatId(catId: string): Observable<TagDefinition[]> {
    return this.getRequest(this.url("tag-categories/" + catId + "/tagdefs"));
  }
  constructor() {
    super();
    this.requestBase = "tags";
  }
}
