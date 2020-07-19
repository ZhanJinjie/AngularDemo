import OutlineNode from '@/models/OutlineNode';
import HttpService from './HttpService'
import { Subscribable } from 'rxjs';
import { Exercise } from '@/models/Exercise';
import { QueryOptions } from '@/queries/QueryOptions';

export default class OutlineNodeService extends HttpService<OutlineNode> {
  removeExercise(id: string,exerciseId: string) {
    const url = this.url(this.requestBase + "/" + id + "/exercises/"+exerciseId);
    return this.deleteRequest(url);
  }
    addExercises(id: string, exIds: string[]) {
        const url = this.url(this.requestBase + "/" + id + "/exercises");
        return this.postRequest(url, exIds);
    }
    findExercises(id: string, options?: QueryOptions) {
        const rurl = this.requestBase + "/" + id + "/exercises";
        return this.getRequest(this.url(rurl, options));
    }
    constructor() {
        super();
        this.requestBase = 'outline-nodes';
    }

    static instance = new OutlineNodeService();
}