import Tag from './Tag';

enum AnswerMethod {
    DANXUAN = "danxuan",
    DUOXUAN = "duoxuan",
    TIANKONG = "tiankong",
    JIEDA = "jieda",
    ZUOTU = "zuotu",
    JISUAN = "jisuan",
    ZONGHE = "zonghe",
    QITA = "qita"

}
interface Exercise {
    id?: string;
    content?: string; //习题内容
    answerMethod?: AnswerMethod;  //答题方式
    answer?: string; //答案
    explain?: string; //试题解析
    difficulty?: number; //难度系数
    tags?: Tag[];//试题标签
    outlineNodeId?: string;
}

export { Exercise, AnswerMethod }