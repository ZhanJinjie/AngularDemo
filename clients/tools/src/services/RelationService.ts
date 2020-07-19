import HttpService from './HttpService';

import Relation from "../models/Relation"
export default class RelationService extends HttpService<Relation>{
    static instance = new RelationService();
    constructor() {
        super();
        this.requestBase = "relations";
    }
}