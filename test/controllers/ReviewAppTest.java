/*
 * Yobi, Project Hosting SW
 *
 * Copyright 2013 NAVER Corp.
 * http://yobi.io
 *
 * @Author Changsung Kim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;


import models.*;
import org.junit.*;
import java.util.Map;
import play.mvc.Result;
import play.test.Helpers;
import utils.JodaDateUtil;
import play.test.FakeApplication;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;


/**
 * {@link controllers.ReviewApp}을 테스트
 */
public class ReviewAppTest {

    protected static FakeApplication app;

    /**
     * 잘못된 URL을 입력하여 not found return을 테스트
     */
    @Test
    public void projectNotFound() {
        Result result = route(fakeRequest(GET, "/admin/dobi/reviews"));
        assertThat(result).isNotNull();
        assertThat(status(result)).isEqualTo(NOT_FOUND);
    }

    /**
     * 비공개 프로젝트에 관리자와 멤버가 아닌 사용자의 접근으로 forbidden return을 테스트
     */
    @Test
    public void projectForbidden() {
        Result result = callAction(
                controllers.routes.ref.ReviewApp.reviews("laziel", "Jindo"),
                fakeRequest(GET, "laziel/jindo/reviews?pageNum=1&state=OPEN&orderDir=desc&orderBy=createdDate")
        );

        assertThat(status(result)).isEqualTo(FORBIDDEN);
    }

    /**
     * 테스트를 위해 메모리 DB로 전환
     */
    @BeforeClass
    public static void beforeClass() {
        Map<String, String> config = support.Config.makeTestConfig();
        app = Helpers.fakeApplication(config);
        Helpers.start(app);

        addTestData();
    }

    /**
     * 테스트를 위해 데이터를 DB에 입력
     */
    private static void addTestData() {
        User admin = User.findByLoginId("admin");
        User laziel = User.findByLoginId("laziel");
        ReviewComment reviewComment;

        Project project = ProjectApp.getProject("yobi", "projectYobi");


        CodeCommentThread codeCommentThread = new CodeCommentThread();
        codeCommentThread.createdDate = JodaDateUtil.now();
        codeCommentThread.commitId = "111";
        codeCommentThread.prevCommitId = "110";
        codeCommentThread.project = project;
        codeCommentThread.author = new UserIdent(admin);
        codeCommentThread.state = CommentThread.ThreadState.OPEN;
        codeCommentThread.save();

        reviewComment = new ReviewComment();
        reviewComment.createdDate = JodaDateUtil.now();
        reviewComment.author = new UserIdent(admin);
        reviewComment.thread = codeCommentThread;
        reviewComment.setContents("Comment #1");
        reviewComment.save();


        codeCommentThread = new CodeCommentThread();
        codeCommentThread.createdDate = JodaDateUtil.now();
        codeCommentThread.commitId = "222";
        codeCommentThread.prevCommitId = "220";
        codeCommentThread.project = project;
        codeCommentThread.author = new UserIdent(admin);
        codeCommentThread.state = CommentThread.ThreadState.CLOSED;
        codeCommentThread.save();
        reviewComment = new ReviewComment();
        reviewComment.createdDate = JodaDateUtil.now();
        reviewComment.author = new UserIdent(admin);
        reviewComment.thread = codeCommentThread;
        reviewComment.setContents("Comment #2");
        reviewComment.save();


        codeCommentThread = new CodeCommentThread();
        codeCommentThread.createdDate = JodaDateUtil.now();
        codeCommentThread.commitId = "333";
        codeCommentThread.prevCommitId = "330";
        codeCommentThread.project = project;
        codeCommentThread.author = new UserIdent(laziel);
        codeCommentThread.state = CommentThread.ThreadState.CLOSED;
        codeCommentThread.save();
        reviewComment = new ReviewComment();
        reviewComment.createdDate = JodaDateUtil.now();
        reviewComment.author = new UserIdent(admin);
        reviewComment.thread = codeCommentThread;
        reviewComment.setContents("Comment #3");
        reviewComment.save();


        codeCommentThread = new CodeCommentThread();
        codeCommentThread.createdDate = JodaDateUtil.now();
        codeCommentThread.commitId = "333";
        codeCommentThread.prevCommitId = "330";
        codeCommentThread.author = new UserIdent(admin);
        codeCommentThread.project = project;
        codeCommentThread.state = CommentThread.ThreadState.CLOSED;
        codeCommentThread.save();
        reviewComment = new ReviewComment();
        reviewComment.createdDate = JodaDateUtil.now();
        reviewComment.author = new UserIdent(admin);
        reviewComment.thread = codeCommentThread;
        reviewComment.setContents("Comment #4");
        reviewComment.save();


        NonRangedCodeCommentThread nonRangedCodeCommentThread = new NonRangedCodeCommentThread();
        nonRangedCodeCommentThread.createdDate = JodaDateUtil.now();
        nonRangedCodeCommentThread.project = project;
        nonRangedCodeCommentThread.commitId = "444";
        nonRangedCodeCommentThread.author = new UserIdent(laziel);
        nonRangedCodeCommentThread.state = CommentThread.ThreadState.OPEN;
        nonRangedCodeCommentThread.save();
        reviewComment = new ReviewComment();
        reviewComment.createdDate = JodaDateUtil.now();
        reviewComment.author = new UserIdent(admin);
        reviewComment.thread = nonRangedCodeCommentThread;
        reviewComment.setContents("Comment #5");
        reviewComment.save();
    }

    @AfterClass
    public static void afterClass() {
        Helpers.stop(app);
    }
}
