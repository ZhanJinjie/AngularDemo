import DlgConfirm from "./DlgConfirm.vue"
import { Subject, Observable } from 'rxjs'
import { Exercise } from '@/models/Exercise';

export default class DlgService {
    static instance = new DlgService();
    confirm!: (title: string, message: string) => Observable<any>;

    openExerciseRelationEditor!: (exercise: Exercise) => void;
}