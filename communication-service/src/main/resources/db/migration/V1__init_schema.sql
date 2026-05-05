CREATE TABLE "forums" (
                          "id" uuid PRIMARY KEY,
                          "forum_name" varchar,
                          "forum_description" text,
                          "is_active" boolean,
                          "class_section_id" uuid UNIQUE NOT NULL,
                          "created_at" timestamptz NOT NULL DEFAULT (now()),
                          "updated_at" timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "discussion_topics" (
                                     "id" uuid PRIMARY KEY,
                                     "forum_id" uuid NOT NULL,
                                     "topic_title" varchar,
                                     "topic_content" text,
                                     "is_locked" boolean,
                                        "create_by" uuid not null,
                                     "last_post_at" timestamptz,
                                     "is_resolved" boolean,
                                     "is_pinned" boolean,
                                     "pinned_at" timestamptz,
                                     "created_at" timestamptz NOT NULL DEFAULT (now()),
                                     "updated_at" timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "topic_tags" (
                              "id" uuid PRIMARY KEY,
                              "tag_name" varchar,
                              "description" varchar,
                              "created_at" timestamptz NOT NULL DEFAULT (now()),
                              "updated_at" timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "user_topic_preference" (
                                         "id" uuid PRIMARY KEY,
                                         "topic_id" uuid NOT NULL,
                                         "user_id" uuid NOT NULL,
                                         "is_pinned" bool DEFAULT false,
                                         "created_at" timestamptz NOT NULL DEFAULT (now()),
                                         "updated_at" timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "discussion_posts" (
                                    "id" uuid PRIMARY KEY,
                                    "discussion_topic_id" uuid NOT NULL,
                                    "user_id" uuid NOT NULL,
                                    "post_content" text,
                                    "reply_post_id" uuid,
                                    "created_at" timestamptz NOT NULL DEFAULT (now()),
                                    "updated_at" timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "post_attachments" (
                                    "id" uuid PRIMARY KEY,
                                    "discussion_post_id" uuid NOT NULL,
                                    "attachment_url" varchar,
                                    "attachment_type" varchar,
                                    "created_at" timestamptz NOT NULL DEFAULT (now()),
                                    "updated_at" timestamptz NOT NULL DEFAULT (now())
);

CREATE UNIQUE INDEX ON "post_attachments" ("id", "discussion_post_id");

ALTER TABLE "discussion_topics" ADD FOREIGN KEY ("forum_id") REFERENCES "forums" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "user_topic_preference" ADD FOREIGN KEY ("topic_id") REFERENCES "discussion_topics" ("id") DEFERRABLE INITIALLY IMMEDIATE;

CREATE TABLE "discussion_topics_topic_tags" (
                                                "discussion_topics_id" uuid,
                                                "topic_tags_id" uuid,
                                                PRIMARY KEY ("discussion_topics_id", "topic_tags_id")
);

ALTER TABLE "discussion_topics_topic_tags" ADD FOREIGN KEY ("discussion_topics_id") REFERENCES "discussion_topics" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "discussion_topics_topic_tags" ADD FOREIGN KEY ("topic_tags_id") REFERENCES "topic_tags" ("id") DEFERRABLE INITIALLY IMMEDIATE;


ALTER TABLE "discussion_posts" ADD FOREIGN KEY ("discussion_topic_id") REFERENCES "discussion_topics" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "discussion_posts" ADD FOREIGN KEY ("reply_post_id") REFERENCES "discussion_posts" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "post_attachments" ADD FOREIGN KEY ("discussion_post_id") REFERENCES "discussion_posts" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "discussion_topics" ADD FOREIGN KEY ("is_locked") REFERENCES "discussion_topics" ("id") DEFERRABLE INITIALLY IMMEDIATE;
