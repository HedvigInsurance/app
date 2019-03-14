/* tslint:disable */

// ====================================================
// START: Typescript template
// ====================================================

// ====================================================
// Scalars
// ====================================================

export type DateTime = any;

export type Uuid = any;

/** A String-representation of `java.time.LocalDate`, ex:  `"2018-09-26"` */
export type LocalDate = any;

export type Object = any;

/** The `Upload` scalar type represents a file upload. */
export type Upload = any;

export type Url = any;

/** The `Long` scalar type represents non-fractional signed whole numeric values. Long can represent values between -(2^63) and 2^63 - 1. */
export type Long = any;

/** Custom scalar representing a Slate rich text AST */
export type RichTextAst = any;

// ====================================================
// Interfaces
// ====================================================

/** An object with an ID */
export interface Node {
  /** The id of the object. */
  id: string;
}

export interface MessageBodyCore {
  type: string;

  id: string;

  text: string;
}

export interface MessageBodyChoicesCore {
  type: string;

  value: string;

  text: string;

  selected: boolean;
}

// ====================================================
// Types
// ====================================================

export interface Query {
  languages: Language[];

  marketingStories: MarketingStory[];

  insurance: Insurance;

  cashback?: Cashback | null;

  cashbackOptions: Cashback[];

  signStatus?: SignStatus | null;

  member: Member;

  gifs: Gif[];

  file: File;

  messages: Message[];

  currentChatResponse?: ChatResponse | null;

  chatState: ChatState;

  avatars?: Avatar[] | null;

  dontPanicPing: string;

  dontPanicSession?: DontPanicSession | null;

  bankAccount?: BankAccount | null;

  chargeDate: LocalDate;

  nextChargeDate?: LocalDate | null;

  registerAccountProcessingStatus: RegisterAccountProcessingStatus;

  directDebitStatus: DirectDebitStatus;
}

export interface Language extends Node {
  status: Status;

  updatedAt: DateTime;

  createdAt: DateTime;

  id: string;

  translations?: Translation[] | null;

  code: string;

  name: string;
}

export interface Translation extends Node {
  status: Status;

  updatedAt: DateTime;

  createdAt: DateTime;

  id: string;

  language?: Language | null;

  project?: Project | null;

  key?: Key | null;

  text: string;
}

export interface Key extends Node {
  status: Status;

  updatedAt: DateTime;

  createdAt: DateTime;

  id: string;

  value: string;

  translations?: Translation[] | null;

  perilCategoryTitle?: PerilCategory | null;

  perilCategoryDescription?: PerilCategory | null;

  perilTitle?: Peril | null;

  perilDescription?: Peril | null;

  description?: string | null;
}

export interface PerilCategory {
  title?: string | null;

  description?: string | null;

  iconUrl?: string | null;

  perils?: Peril[] | null;
}

export interface Peril {
  id?: string | null;

  title?: string | null;

  imageUrl?: string | null;

  description?: string | null;
}

export interface MarketingStory extends Node {
  status: Status;

  updatedAt: DateTime;

  createdAt: DateTime;

  id: string;

  asset?: Asset | null;

  duration?: number | null;

  importance?: number | null;

  backgroundColor: HedvigColor;
}

export interface Asset extends Node {
  status: Status;

  updatedAt: DateTime;

  createdAt: DateTime;

  id: string;

  handle: string;

  fileName: string;

  height?: number | null;

  width?: number | null;

  size?: number | null;

  mimeType?: string | null;

  assetMarketingStory?: MarketingStory[] | null;
  /** Get the url for the asset with provided transformations applied. */
  url: string;
}

export interface Insurance {
  id?: Uuid | null;

  address?: string | null;

  postalNumber?: string | null;

  monthlyCost?: number | null;

  safetyIncreasers?: string[] | null;

  personsInHousehold?: number | null;

  certificateUrl?: string | null;

  status: InsuranceStatus;

  type?: InsuranceType | null;

  activeFrom?: LocalDate | null;

  insuredAtOtherCompany?: boolean | null;

  presaleInformationUrl?: string | null;

  policyUrl?: string | null;

  currentInsurerName?: string | null;

  requestedStartDate?: LocalDate | null;

  perilCategories?: PerilCategory[] | null;
}

export interface Cashback {
  id?: string | null;

  name?: string | null;

  imageUrl?: string | null;

  selectedUrl?: string | null;

  description?: string | null;

  title?: string | null;

  paragraph?: string | null;
}

export interface SignStatus {
  collectStatus?: CollectStatus | null;

  signState?: SignState | null;
}

export interface CollectStatus {
  status?: BankIdStatus | null;

  code?: string | null;
}

export interface Member {
  id?: string | null;

  firstName?: string | null;

  lastName?: string | null;

  email?: string | null;

  phoneNumber?: string | null;
}

export interface Gif {
  url?: string | null;
}

export interface File {
  /** signedUrl is valid for 30 minutes after upload, don't hang on to this. */
  signedUrl: string;
  /** S3 key that can be used to retreive new signed urls in the future. */
  key: string;
}

export interface Message {
  globalId: string;

  id: string;

  body: MessageBody;

  header: MessageHeader;
}

export interface MessageBodySingleSelect extends MessageBodyCore {
  type: string;

  id: string;

  text: string;

  choices?: MessageBodyChoices[] | null;
}

export interface MessageBodyChoicesUndefined extends MessageBodyChoicesCore {
  type: string;

  value: string;

  text: string;

  selected: boolean;
}

export interface MessageBodyChoicesSelection extends MessageBodyChoicesCore {
  type: string;

  value: string;

  text: string;

  selected: boolean;

  clearable?: boolean | null;
}

export interface MessageBodyChoicesLink extends MessageBodyChoicesCore {
  type: string;

  value: string;

  text: string;

  selected: boolean;

  view?: MessageBodyChoicesLinkView | null;

  appUrl?: string | null;

  webUrl?: string | null;
}

export interface MessageBodyMultipleSelect extends MessageBodyCore {
  type: string;

  id: string;

  text: string;

  choices?: MessageBodyChoices[] | null;
}

export interface MessageBodyText extends MessageBodyCore {
  type: string;

  id: string;

  text: string;
}

export interface MessageBodyNumber extends MessageBodyCore {
  type: string;

  id: string;

  text: string;
}

export interface MessageBodyAudio extends MessageBodyCore {
  type: string;

  id: string;

  text: string;

  url?: string | null;
}

export interface MessageBodyBankIdCollect extends MessageBodyCore {
  type: string;

  id: string;

  text: string;

  referenceId?: string | null;
}

export interface MessageBodyFile extends MessageBodyCore {
  type: string;

  id: string;

  text: string;

  key?: string | null;

  mimeType?: string | null;
}

export interface MessageBodyParagraph extends MessageBodyCore {
  type: string;

  id: string;

  text: string;
}

export interface MessageBodyUndefined extends MessageBodyCore {
  type: string;

  id: string;

  text: string;
}

export interface MessageHeader {
  messageId: string;

  fromMyself: boolean;

  timeStamp: string;

  richTextChatCompatible: boolean;

  editAllowed: boolean;

  shouldRequestPushNotifications: boolean;

  pollingInterval: number;

  loadingIndicator?: string | null;
}

export interface ChatResponse {
  globalId: string;

  id: string;

  body?: MessageBody | null;

  header?: MessageHeader | null;
}

export interface ChatState {
  ongoingClaim: boolean;

  showOfferScreen: boolean;

  onboardingDone: boolean;
}

export interface Avatar {
  name: string;

  URL: string;

  width: number;

  height: number;

  duration: number;

  data?: Object | null;
}

export interface DontPanicSession {
  id: string;

  name: string;

  lastName?: string | null;

  email?: string | null;

  currentInsurer?: string | null;

  chatMessages: DontPanicChatMessage[];
}

export interface DontPanicChatMessage {
  id: string;

  who: string;

  text: string;

  session: DontPanicSession;

  isHedvig: boolean;

  type: string;
}

export interface BankAccount {
  bankName: string;

  descriptor: string;

  directDebitStatus?: DirectDebitStatus | null;
}

export interface Mutation {
  logout: boolean;

  createSession: string;

  createSessionV2?: SessionInformation | null;

  createOffer?: boolean | null;

  signOffer?: boolean | null;

  uploadFile: File;

  selectCashbackOption: Cashback;

  offerClosed: boolean;

  startDirectDebitRegistration: Url;

  sendChatTextResponse: boolean;

  sendChatSingleSelectResponse: boolean;

  sendChatFileResponse: boolean;

  sendChatAudioResponse: boolean;

  resetConversation: boolean;

  editLastResponse: boolean;

  updateEmail: Member;

  updatePhoneNumber: Member;

  requestStartDate: boolean;

  createDontPanicSession: DontPanicSession;

  addDontPanicChatMessage: DontPanicChatMessage;

  registerDirectDebit: DirectDebitResponse;

  cancelDirectDebitRequest: CancelDirectDebitStatus;
}

export interface SessionInformation {
  token: string;

  memberId: string;
}

export interface DirectDebitResponse {
  url: string;

  orderId: string;
}

export interface Subscription {
  offer?: OfferEvent | null;

  signStatus?: SignEvent | null;

  messages?: Message[] | null;

  currentChatResponse?: ChatResponse | null;

  chatState: ChatState;
}

export interface OfferEvent {
  status: OfferStatus;

  insurance?: Insurance | null;
}

export interface SignEvent {
  status?: SignStatus | null;
}

export interface Color extends Node {
  updatedAt: DateTime;

  createdAt: DateTime;

  id: string;
}

export interface Location extends Node {
  updatedAt: DateTime;

  createdAt: DateTime;

  id: string;
}
/** A connection to a list of items. */
export interface AssetConnection {
  /** Information to aid in pagination. */
  pageInfo: PageInfo;
  /** A list of edges. */
  edges: AssetEdge[];

  aggregate: AggregateAsset;
}
/** Information about pagination in a connection. */
export interface PageInfo {
  /** When paginating forwards, are there more items? */
  hasNextPage: boolean;
  /** When paginating backwards, are there more items? */
  hasPreviousPage: boolean;
  /** When paginating backwards, the cursor to continue. */
  startCursor?: string | null;
  /** When paginating forwards, the cursor to continue. */
  endCursor?: string | null;
}
/** An edge in a connection. */
export interface AssetEdge {
  /** The item at the end of the edge. */
  node: Asset;
  /** A cursor for use in pagination. */
  cursor: string;
}

export interface AggregateAsset {
  count: number;
}
/** A connection to a list of items. */
export interface ColorConnection {
  /** Information to aid in pagination. */
  pageInfo: PageInfo;
  /** A list of edges. */
  edges: ColorEdge[];

  aggregate: AggregateColor;
}
/** An edge in a connection. */
export interface ColorEdge {
  /** The item at the end of the edge. */
  node: Color;
  /** A cursor for use in pagination. */
  cursor: string;
}

export interface AggregateColor {
  count: number;
}
/** A connection to a list of items. */
export interface LocationConnection {
  /** Information to aid in pagination. */
  pageInfo: PageInfo;
  /** A list of edges. */
  edges: LocationEdge[];

  aggregate: AggregateLocation;
}
/** An edge in a connection. */
export interface LocationEdge {
  /** The item at the end of the edge. */
  node: Location;
  /** A cursor for use in pagination. */
  cursor: string;
}

export interface AggregateLocation {
  count: number;
}
/** A connection to a list of items. */
export interface LanguageConnection {
  /** Information to aid in pagination. */
  pageInfo: PageInfo;
  /** A list of edges. */
  edges: LanguageEdge[];

  aggregate: AggregateLanguage;
}
/** An edge in a connection. */
export interface LanguageEdge {
  /** The item at the end of the edge. */
  node: Language;
  /** A cursor for use in pagination. */
  cursor: string;
}

export interface AggregateLanguage {
  count: number;
}
/** A connection to a list of items. */
export interface KeyConnection {
  /** Information to aid in pagination. */
  pageInfo: PageInfo;
  /** A list of edges. */
  edges: KeyEdge[];

  aggregate: AggregateKey;
}
/** An edge in a connection. */
export interface KeyEdge {
  /** The item at the end of the edge. */
  node: Key;
  /** A cursor for use in pagination. */
  cursor: string;
}

export interface AggregateKey {
  count: number;
}
/** A connection to a list of items. */
export interface TranslationConnection {
  /** Information to aid in pagination. */
  pageInfo: PageInfo;
  /** A list of edges. */
  edges: TranslationEdge[];

  aggregate: AggregateTranslation;
}
/** An edge in a connection. */
export interface TranslationEdge {
  /** The item at the end of the edge. */
  node: Translation;
  /** A cursor for use in pagination. */
  cursor: string;
}

export interface AggregateTranslation {
  count: number;
}
/** A connection to a list of items. */
export interface PerilConnection {
  /** Information to aid in pagination. */
  pageInfo: PageInfo;
  /** A list of edges. */
  edges: PerilEdge[];

  aggregate: AggregatePeril;
}
/** An edge in a connection. */
export interface PerilEdge {
  /** The item at the end of the edge. */
  node: Peril;
  /** A cursor for use in pagination. */
  cursor: string;
}

export interface AggregatePeril {
  count: number;
}
/** A connection to a list of items. */
export interface PerilCategoryConnection {
  /** Information to aid in pagination. */
  pageInfo: PageInfo;
  /** A list of edges. */
  edges: PerilCategoryEdge[];

  aggregate: AggregatePerilCategory;
}
/** An edge in a connection. */
export interface PerilCategoryEdge {
  /** The item at the end of the edge. */
  node: PerilCategory;
  /** A cursor for use in pagination. */
  cursor: string;
}

export interface AggregatePerilCategory {
  count: number;
}
/** A connection to a list of items. */
export interface MarketingStoryConnection {
  /** Information to aid in pagination. */
  pageInfo: PageInfo;
  /** A list of edges. */
  edges: MarketingStoryEdge[];

  aggregate: AggregateMarketingStory;
}
/** An edge in a connection. */
export interface MarketingStoryEdge {
  /** The item at the end of the edge. */
  node: MarketingStory;
  /** A cursor for use in pagination. */
  cursor: string;
}

export interface AggregateMarketingStory {
  count: number;
}

export interface BatchPayload {
  /** The number of nodes that have been affected by the Batch operation. */
  count: Long;
}

export interface AssetPreviousValues {
  status: Status;

  updatedAt: DateTime;

  createdAt: DateTime;

  id: string;

  handle: string;

  fileName: string;

  height?: number | null;

  width?: number | null;

  size?: number | null;

  mimeType?: string | null;
}

export interface AssetSubscriptionPayload {
  mutation: MutationType;

  node?: Asset | null;

  updatedFields?: string[] | null;

  previousValues?: AssetPreviousValues | null;
}

export interface ColorPreviousValues {
  updatedAt: DateTime;

  createdAt: DateTime;

  id: string;
}

export interface ColorSubscriptionPayload {
  mutation: MutationType;

  node?: Color | null;

  updatedFields?: string[] | null;

  previousValues?: ColorPreviousValues | null;
}

export interface KeyPreviousValues {
  status: Status;

  updatedAt: DateTime;

  createdAt: DateTime;

  id: string;

  value: string;

  description?: string | null;
}

export interface KeySubscriptionPayload {
  mutation: MutationType;

  node?: Key | null;

  updatedFields?: string[] | null;

  previousValues?: KeyPreviousValues | null;
}

export interface LanguagePreviousValues {
  status: Status;

  updatedAt: DateTime;

  createdAt: DateTime;

  id: string;

  code: string;

  name: string;
}

export interface LanguageSubscriptionPayload {
  mutation: MutationType;

  node?: Language | null;

  updatedFields?: string[] | null;

  previousValues?: LanguagePreviousValues | null;
}

export interface LocationPreviousValues {
  updatedAt: DateTime;

  createdAt: DateTime;

  id: string;
}

export interface LocationSubscriptionPayload {
  mutation: MutationType;

  node?: Location | null;

  updatedFields?: string[] | null;

  previousValues?: LocationPreviousValues | null;
}

export interface MarketingStoryPreviousValues {
  status: Status;

  updatedAt: DateTime;

  createdAt: DateTime;

  id: string;

  duration?: number | null;

  importance?: number | null;

  backgroundColor: HedvigColor;
}

export interface MarketingStorySubscriptionPayload {
  mutation: MutationType;

  node?: MarketingStory | null;

  updatedFields?: string[] | null;

  previousValues?: MarketingStoryPreviousValues | null;
}

export interface PerilCategoryPreviousValues {
  status: Status;

  updatedAt: DateTime;

  createdAt: DateTime;

  id: string;

  name: string;
}

export interface PerilCategorySubscriptionPayload {
  mutation: MutationType;

  node?: PerilCategory | null;

  updatedFields?: string[] | null;

  previousValues?: PerilCategoryPreviousValues | null;
}

export interface PerilPreviousValues {
  status: Status;

  updatedAt: DateTime;

  createdAt: DateTime;

  id: string;
}

export interface PerilSubscriptionPayload {
  mutation: MutationType;

  node?: Peril | null;

  updatedFields?: string[] | null;

  previousValues?: PerilPreviousValues | null;
}

export interface RichText {
  raw?: RichTextAst | null;

  html?: string | null;

  markdown?: string | null;
}

export interface TranslationPreviousValues {
  status: Status;

  updatedAt: DateTime;

  createdAt: DateTime;

  id: string;

  project?: Project | null;

  text: string;
}

export interface TranslationSubscriptionPayload {
  mutation: MutationType;

  node?: Translation | null;

  updatedFields?: string[] | null;

  previousValues?: TranslationPreviousValues | null;
}

// ====================================================
// InputTypes
// ====================================================

export interface LanguageWhereInput {
  /** Logical AND on all given filters. */
  AND?: LanguageWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: LanguageWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: LanguageWhereInput[] | null;

  status?: Status | null;
  /** All values that are not equal to given value. */
  status_not?: Status | null;
  /** All values that are contained in given list. */
  status_in?: Status[] | null;
  /** All values that are not contained in given list. */
  status_not_in?: Status[] | null;

  updatedAt?: DateTime | null;
  /** All values that are not equal to given value. */
  updatedAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  updatedAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  updatedAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  updatedAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  updatedAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  updatedAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  updatedAt_gte?: DateTime | null;

  createdAt?: DateTime | null;
  /** All values that are not equal to given value. */
  createdAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  createdAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  createdAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  createdAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  createdAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  createdAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  createdAt_gte?: DateTime | null;

  id?: string | null;
  /** All values that are not equal to given value. */
  id_not?: string | null;
  /** All values that are contained in given list. */
  id_in?: string[] | null;
  /** All values that are not contained in given list. */
  id_not_in?: string[] | null;
  /** All values less than the given value. */
  id_lt?: string | null;
  /** All values less than or equal the given value. */
  id_lte?: string | null;
  /** All values greater than the given value. */
  id_gt?: string | null;
  /** All values greater than or equal the given value. */
  id_gte?: string | null;
  /** All values containing the given string. */
  id_contains?: string | null;
  /** All values not containing the given string. */
  id_not_contains?: string | null;
  /** All values starting with the given string. */
  id_starts_with?: string | null;
  /** All values not starting with the given string. */
  id_not_starts_with?: string | null;
  /** All values ending with the given string. */
  id_ends_with?: string | null;
  /** All values not ending with the given string. */
  id_not_ends_with?: string | null;

  code?: string | null;
  /** All values that are not equal to given value. */
  code_not?: string | null;
  /** All values that are contained in given list. */
  code_in?: string[] | null;
  /** All values that are not contained in given list. */
  code_not_in?: string[] | null;
  /** All values less than the given value. */
  code_lt?: string | null;
  /** All values less than or equal the given value. */
  code_lte?: string | null;
  /** All values greater than the given value. */
  code_gt?: string | null;
  /** All values greater than or equal the given value. */
  code_gte?: string | null;
  /** All values containing the given string. */
  code_contains?: string | null;
  /** All values not containing the given string. */
  code_not_contains?: string | null;
  /** All values starting with the given string. */
  code_starts_with?: string | null;
  /** All values not starting with the given string. */
  code_not_starts_with?: string | null;
  /** All values ending with the given string. */
  code_ends_with?: string | null;
  /** All values not ending with the given string. */
  code_not_ends_with?: string | null;

  name?: string | null;
  /** All values that are not equal to given value. */
  name_not?: string | null;
  /** All values that are contained in given list. */
  name_in?: string[] | null;
  /** All values that are not contained in given list. */
  name_not_in?: string[] | null;
  /** All values less than the given value. */
  name_lt?: string | null;
  /** All values less than or equal the given value. */
  name_lte?: string | null;
  /** All values greater than the given value. */
  name_gt?: string | null;
  /** All values greater than or equal the given value. */
  name_gte?: string | null;
  /** All values containing the given string. */
  name_contains?: string | null;
  /** All values not containing the given string. */
  name_not_contains?: string | null;
  /** All values starting with the given string. */
  name_starts_with?: string | null;
  /** All values not starting with the given string. */
  name_not_starts_with?: string | null;
  /** All values ending with the given string. */
  name_ends_with?: string | null;
  /** All values not ending with the given string. */
  name_not_ends_with?: string | null;

  translations_every?: TranslationWhereInput | null;

  translations_some?: TranslationWhereInput | null;

  translations_none?: TranslationWhereInput | null;
}

export interface TranslationWhereInput {
  /** Logical AND on all given filters. */
  AND?: TranslationWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: TranslationWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: TranslationWhereInput[] | null;

  status?: Status | null;
  /** All values that are not equal to given value. */
  status_not?: Status | null;
  /** All values that are contained in given list. */
  status_in?: Status[] | null;
  /** All values that are not contained in given list. */
  status_not_in?: Status[] | null;

  updatedAt?: DateTime | null;
  /** All values that are not equal to given value. */
  updatedAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  updatedAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  updatedAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  updatedAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  updatedAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  updatedAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  updatedAt_gte?: DateTime | null;

  createdAt?: DateTime | null;
  /** All values that are not equal to given value. */
  createdAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  createdAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  createdAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  createdAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  createdAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  createdAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  createdAt_gte?: DateTime | null;

  id?: string | null;
  /** All values that are not equal to given value. */
  id_not?: string | null;
  /** All values that are contained in given list. */
  id_in?: string[] | null;
  /** All values that are not contained in given list. */
  id_not_in?: string[] | null;
  /** All values less than the given value. */
  id_lt?: string | null;
  /** All values less than or equal the given value. */
  id_lte?: string | null;
  /** All values greater than the given value. */
  id_gt?: string | null;
  /** All values greater than or equal the given value. */
  id_gte?: string | null;
  /** All values containing the given string. */
  id_contains?: string | null;
  /** All values not containing the given string. */
  id_not_contains?: string | null;
  /** All values starting with the given string. */
  id_starts_with?: string | null;
  /** All values not starting with the given string. */
  id_not_starts_with?: string | null;
  /** All values ending with the given string. */
  id_ends_with?: string | null;
  /** All values not ending with the given string. */
  id_not_ends_with?: string | null;

  project?: Project | null;
  /** All values that are not equal to given value. */
  project_not?: Project | null;
  /** All values that are contained in given list. */
  project_in?: Project[] | null;
  /** All values that are not contained in given list. */
  project_not_in?: Project[] | null;

  text?: string | null;
  /** All values that are not equal to given value. */
  text_not?: string | null;
  /** All values that are contained in given list. */
  text_in?: string[] | null;
  /** All values that are not contained in given list. */
  text_not_in?: string[] | null;
  /** All values less than the given value. */
  text_lt?: string | null;
  /** All values less than or equal the given value. */
  text_lte?: string | null;
  /** All values greater than the given value. */
  text_gt?: string | null;
  /** All values greater than or equal the given value. */
  text_gte?: string | null;
  /** All values containing the given string. */
  text_contains?: string | null;
  /** All values not containing the given string. */
  text_not_contains?: string | null;
  /** All values starting with the given string. */
  text_starts_with?: string | null;
  /** All values not starting with the given string. */
  text_not_starts_with?: string | null;
  /** All values ending with the given string. */
  text_ends_with?: string | null;
  /** All values not ending with the given string. */
  text_not_ends_with?: string | null;

  language?: LanguageWhereInput | null;

  key?: KeyWhereInput | null;
}

export interface KeyWhereInput {
  /** Logical AND on all given filters. */
  AND?: KeyWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: KeyWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: KeyWhereInput[] | null;

  status?: Status | null;
  /** All values that are not equal to given value. */
  status_not?: Status | null;
  /** All values that are contained in given list. */
  status_in?: Status[] | null;
  /** All values that are not contained in given list. */
  status_not_in?: Status[] | null;

  updatedAt?: DateTime | null;
  /** All values that are not equal to given value. */
  updatedAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  updatedAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  updatedAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  updatedAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  updatedAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  updatedAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  updatedAt_gte?: DateTime | null;

  createdAt?: DateTime | null;
  /** All values that are not equal to given value. */
  createdAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  createdAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  createdAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  createdAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  createdAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  createdAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  createdAt_gte?: DateTime | null;

  id?: string | null;
  /** All values that are not equal to given value. */
  id_not?: string | null;
  /** All values that are contained in given list. */
  id_in?: string[] | null;
  /** All values that are not contained in given list. */
  id_not_in?: string[] | null;
  /** All values less than the given value. */
  id_lt?: string | null;
  /** All values less than or equal the given value. */
  id_lte?: string | null;
  /** All values greater than the given value. */
  id_gt?: string | null;
  /** All values greater than or equal the given value. */
  id_gte?: string | null;
  /** All values containing the given string. */
  id_contains?: string | null;
  /** All values not containing the given string. */
  id_not_contains?: string | null;
  /** All values starting with the given string. */
  id_starts_with?: string | null;
  /** All values not starting with the given string. */
  id_not_starts_with?: string | null;
  /** All values ending with the given string. */
  id_ends_with?: string | null;
  /** All values not ending with the given string. */
  id_not_ends_with?: string | null;

  value?: string | null;
  /** All values that are not equal to given value. */
  value_not?: string | null;
  /** All values that are contained in given list. */
  value_in?: string[] | null;
  /** All values that are not contained in given list. */
  value_not_in?: string[] | null;
  /** All values less than the given value. */
  value_lt?: string | null;
  /** All values less than or equal the given value. */
  value_lte?: string | null;
  /** All values greater than the given value. */
  value_gt?: string | null;
  /** All values greater than or equal the given value. */
  value_gte?: string | null;
  /** All values containing the given string. */
  value_contains?: string | null;
  /** All values not containing the given string. */
  value_not_contains?: string | null;
  /** All values starting with the given string. */
  value_starts_with?: string | null;
  /** All values not starting with the given string. */
  value_not_starts_with?: string | null;
  /** All values ending with the given string. */
  value_ends_with?: string | null;
  /** All values not ending with the given string. */
  value_not_ends_with?: string | null;

  description?: string | null;
  /** All values that are not equal to given value. */
  description_not?: string | null;
  /** All values that are contained in given list. */
  description_in?: string[] | null;
  /** All values that are not contained in given list. */
  description_not_in?: string[] | null;
  /** All values less than the given value. */
  description_lt?: string | null;
  /** All values less than or equal the given value. */
  description_lte?: string | null;
  /** All values greater than the given value. */
  description_gt?: string | null;
  /** All values greater than or equal the given value. */
  description_gte?: string | null;
  /** All values containing the given string. */
  description_contains?: string | null;
  /** All values not containing the given string. */
  description_not_contains?: string | null;
  /** All values starting with the given string. */
  description_starts_with?: string | null;
  /** All values not starting with the given string. */
  description_not_starts_with?: string | null;
  /** All values ending with the given string. */
  description_ends_with?: string | null;
  /** All values not ending with the given string. */
  description_not_ends_with?: string | null;

  translations_every?: TranslationWhereInput | null;

  translations_some?: TranslationWhereInput | null;

  translations_none?: TranslationWhereInput | null;

  perilCategoryTitle?: PerilCategoryWhereInput | null;

  perilCategoryDescription?: PerilCategoryWhereInput | null;

  perilTitle?: PerilWhereInput | null;

  perilDescription?: PerilWhereInput | null;
}

export interface PerilCategoryWhereInput {
  /** Logical AND on all given filters. */
  AND?: PerilCategoryWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: PerilCategoryWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: PerilCategoryWhereInput[] | null;

  status?: Status | null;
  /** All values that are not equal to given value. */
  status_not?: Status | null;
  /** All values that are contained in given list. */
  status_in?: Status[] | null;
  /** All values that are not contained in given list. */
  status_not_in?: Status[] | null;

  updatedAt?: DateTime | null;
  /** All values that are not equal to given value. */
  updatedAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  updatedAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  updatedAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  updatedAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  updatedAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  updatedAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  updatedAt_gte?: DateTime | null;

  createdAt?: DateTime | null;
  /** All values that are not equal to given value. */
  createdAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  createdAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  createdAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  createdAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  createdAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  createdAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  createdAt_gte?: DateTime | null;

  id?: string | null;
  /** All values that are not equal to given value. */
  id_not?: string | null;
  /** All values that are contained in given list. */
  id_in?: string[] | null;
  /** All values that are not contained in given list. */
  id_not_in?: string[] | null;
  /** All values less than the given value. */
  id_lt?: string | null;
  /** All values less than or equal the given value. */
  id_lte?: string | null;
  /** All values greater than the given value. */
  id_gt?: string | null;
  /** All values greater than or equal the given value. */
  id_gte?: string | null;
  /** All values containing the given string. */
  id_contains?: string | null;
  /** All values not containing the given string. */
  id_not_contains?: string | null;
  /** All values starting with the given string. */
  id_starts_with?: string | null;
  /** All values not starting with the given string. */
  id_not_starts_with?: string | null;
  /** All values ending with the given string. */
  id_ends_with?: string | null;
  /** All values not ending with the given string. */
  id_not_ends_with?: string | null;

  name?: string | null;
  /** All values that are not equal to given value. */
  name_not?: string | null;
  /** All values that are contained in given list. */
  name_in?: string[] | null;
  /** All values that are not contained in given list. */
  name_not_in?: string[] | null;
  /** All values less than the given value. */
  name_lt?: string | null;
  /** All values less than or equal the given value. */
  name_lte?: string | null;
  /** All values greater than the given value. */
  name_gt?: string | null;
  /** All values greater than or equal the given value. */
  name_gte?: string | null;
  /** All values containing the given string. */
  name_contains?: string | null;
  /** All values not containing the given string. */
  name_not_contains?: string | null;
  /** All values starting with the given string. */
  name_starts_with?: string | null;
  /** All values not starting with the given string. */
  name_not_starts_with?: string | null;
  /** All values ending with the given string. */
  name_ends_with?: string | null;
  /** All values not ending with the given string. */
  name_not_ends_with?: string | null;

  perils_every?: PerilWhereInput | null;

  perils_some?: PerilWhereInput | null;

  perils_none?: PerilWhereInput | null;

  titleKey?: KeyWhereInput | null;

  descriptionKey?: KeyWhereInput | null;
}

export interface PerilWhereInput {
  /** Logical AND on all given filters. */
  AND?: PerilWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: PerilWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: PerilWhereInput[] | null;

  status?: Status | null;
  /** All values that are not equal to given value. */
  status_not?: Status | null;
  /** All values that are contained in given list. */
  status_in?: Status[] | null;
  /** All values that are not contained in given list. */
  status_not_in?: Status[] | null;

  updatedAt?: DateTime | null;
  /** All values that are not equal to given value. */
  updatedAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  updatedAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  updatedAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  updatedAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  updatedAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  updatedAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  updatedAt_gte?: DateTime | null;

  createdAt?: DateTime | null;
  /** All values that are not equal to given value. */
  createdAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  createdAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  createdAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  createdAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  createdAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  createdAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  createdAt_gte?: DateTime | null;

  id?: string | null;
  /** All values that are not equal to given value. */
  id_not?: string | null;
  /** All values that are contained in given list. */
  id_in?: string[] | null;
  /** All values that are not contained in given list. */
  id_not_in?: string[] | null;
  /** All values less than the given value. */
  id_lt?: string | null;
  /** All values less than or equal the given value. */
  id_lte?: string | null;
  /** All values greater than the given value. */
  id_gt?: string | null;
  /** All values greater than or equal the given value. */
  id_gte?: string | null;
  /** All values containing the given string. */
  id_contains?: string | null;
  /** All values not containing the given string. */
  id_not_contains?: string | null;
  /** All values starting with the given string. */
  id_starts_with?: string | null;
  /** All values not starting with the given string. */
  id_not_starts_with?: string | null;
  /** All values ending with the given string. */
  id_ends_with?: string | null;
  /** All values not ending with the given string. */
  id_not_ends_with?: string | null;

  perilCategory?: PerilCategoryWhereInput | null;

  titleKey?: KeyWhereInput | null;

  descriptionKey?: KeyWhereInput | null;
}

export interface MarketingStoryWhereInput {
  /** Logical AND on all given filters. */
  AND?: MarketingStoryWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: MarketingStoryWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: MarketingStoryWhereInput[] | null;

  status?: Status | null;
  /** All values that are not equal to given value. */
  status_not?: Status | null;
  /** All values that are contained in given list. */
  status_in?: Status[] | null;
  /** All values that are not contained in given list. */
  status_not_in?: Status[] | null;

  updatedAt?: DateTime | null;
  /** All values that are not equal to given value. */
  updatedAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  updatedAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  updatedAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  updatedAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  updatedAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  updatedAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  updatedAt_gte?: DateTime | null;

  createdAt?: DateTime | null;
  /** All values that are not equal to given value. */
  createdAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  createdAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  createdAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  createdAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  createdAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  createdAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  createdAt_gte?: DateTime | null;

  id?: string | null;
  /** All values that are not equal to given value. */
  id_not?: string | null;
  /** All values that are contained in given list. */
  id_in?: string[] | null;
  /** All values that are not contained in given list. */
  id_not_in?: string[] | null;
  /** All values less than the given value. */
  id_lt?: string | null;
  /** All values less than or equal the given value. */
  id_lte?: string | null;
  /** All values greater than the given value. */
  id_gt?: string | null;
  /** All values greater than or equal the given value. */
  id_gte?: string | null;
  /** All values containing the given string. */
  id_contains?: string | null;
  /** All values not containing the given string. */
  id_not_contains?: string | null;
  /** All values starting with the given string. */
  id_starts_with?: string | null;
  /** All values not starting with the given string. */
  id_not_starts_with?: string | null;
  /** All values ending with the given string. */
  id_ends_with?: string | null;
  /** All values not ending with the given string. */
  id_not_ends_with?: string | null;

  duration?: number | null;
  /** All values that are not equal to given value. */
  duration_not?: number | null;
  /** All values that are contained in given list. */
  duration_in?: number[] | null;
  /** All values that are not contained in given list. */
  duration_not_in?: number[] | null;
  /** All values less than the given value. */
  duration_lt?: number | null;
  /** All values less than or equal the given value. */
  duration_lte?: number | null;
  /** All values greater than the given value. */
  duration_gt?: number | null;
  /** All values greater than or equal the given value. */
  duration_gte?: number | null;

  importance?: number | null;
  /** All values that are not equal to given value. */
  importance_not?: number | null;
  /** All values that are contained in given list. */
  importance_in?: number[] | null;
  /** All values that are not contained in given list. */
  importance_not_in?: number[] | null;
  /** All values less than the given value. */
  importance_lt?: number | null;
  /** All values less than or equal the given value. */
  importance_lte?: number | null;
  /** All values greater than the given value. */
  importance_gt?: number | null;
  /** All values greater than or equal the given value. */
  importance_gte?: number | null;

  backgroundColor?: HedvigColor | null;
  /** All values that are not equal to given value. */
  backgroundColor_not?: HedvigColor | null;
  /** All values that are contained in given list. */
  backgroundColor_in?: HedvigColor[] | null;
  /** All values that are not contained in given list. */
  backgroundColor_not_in?: HedvigColor[] | null;

  asset?: AssetWhereInput | null;
}

export interface AssetWhereInput {
  /** Logical AND on all given filters. */
  AND?: AssetWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: AssetWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: AssetWhereInput[] | null;

  status?: Status | null;
  /** All values that are not equal to given value. */
  status_not?: Status | null;
  /** All values that are contained in given list. */
  status_in?: Status[] | null;
  /** All values that are not contained in given list. */
  status_not_in?: Status[] | null;

  updatedAt?: DateTime | null;
  /** All values that are not equal to given value. */
  updatedAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  updatedAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  updatedAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  updatedAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  updatedAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  updatedAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  updatedAt_gte?: DateTime | null;

  createdAt?: DateTime | null;
  /** All values that are not equal to given value. */
  createdAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  createdAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  createdAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  createdAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  createdAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  createdAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  createdAt_gte?: DateTime | null;

  id?: string | null;
  /** All values that are not equal to given value. */
  id_not?: string | null;
  /** All values that are contained in given list. */
  id_in?: string[] | null;
  /** All values that are not contained in given list. */
  id_not_in?: string[] | null;
  /** All values less than the given value. */
  id_lt?: string | null;
  /** All values less than or equal the given value. */
  id_lte?: string | null;
  /** All values greater than the given value. */
  id_gt?: string | null;
  /** All values greater than or equal the given value. */
  id_gte?: string | null;
  /** All values containing the given string. */
  id_contains?: string | null;
  /** All values not containing the given string. */
  id_not_contains?: string | null;
  /** All values starting with the given string. */
  id_starts_with?: string | null;
  /** All values not starting with the given string. */
  id_not_starts_with?: string | null;
  /** All values ending with the given string. */
  id_ends_with?: string | null;
  /** All values not ending with the given string. */
  id_not_ends_with?: string | null;

  handle?: string | null;
  /** All values that are not equal to given value. */
  handle_not?: string | null;
  /** All values that are contained in given list. */
  handle_in?: string[] | null;
  /** All values that are not contained in given list. */
  handle_not_in?: string[] | null;
  /** All values less than the given value. */
  handle_lt?: string | null;
  /** All values less than or equal the given value. */
  handle_lte?: string | null;
  /** All values greater than the given value. */
  handle_gt?: string | null;
  /** All values greater than or equal the given value. */
  handle_gte?: string | null;
  /** All values containing the given string. */
  handle_contains?: string | null;
  /** All values not containing the given string. */
  handle_not_contains?: string | null;
  /** All values starting with the given string. */
  handle_starts_with?: string | null;
  /** All values not starting with the given string. */
  handle_not_starts_with?: string | null;
  /** All values ending with the given string. */
  handle_ends_with?: string | null;
  /** All values not ending with the given string. */
  handle_not_ends_with?: string | null;

  fileName?: string | null;
  /** All values that are not equal to given value. */
  fileName_not?: string | null;
  /** All values that are contained in given list. */
  fileName_in?: string[] | null;
  /** All values that are not contained in given list. */
  fileName_not_in?: string[] | null;
  /** All values less than the given value. */
  fileName_lt?: string | null;
  /** All values less than or equal the given value. */
  fileName_lte?: string | null;
  /** All values greater than the given value. */
  fileName_gt?: string | null;
  /** All values greater than or equal the given value. */
  fileName_gte?: string | null;
  /** All values containing the given string. */
  fileName_contains?: string | null;
  /** All values not containing the given string. */
  fileName_not_contains?: string | null;
  /** All values starting with the given string. */
  fileName_starts_with?: string | null;
  /** All values not starting with the given string. */
  fileName_not_starts_with?: string | null;
  /** All values ending with the given string. */
  fileName_ends_with?: string | null;
  /** All values not ending with the given string. */
  fileName_not_ends_with?: string | null;

  height?: number | null;
  /** All values that are not equal to given value. */
  height_not?: number | null;
  /** All values that are contained in given list. */
  height_in?: number[] | null;
  /** All values that are not contained in given list. */
  height_not_in?: number[] | null;
  /** All values less than the given value. */
  height_lt?: number | null;
  /** All values less than or equal the given value. */
  height_lte?: number | null;
  /** All values greater than the given value. */
  height_gt?: number | null;
  /** All values greater than or equal the given value. */
  height_gte?: number | null;

  width?: number | null;
  /** All values that are not equal to given value. */
  width_not?: number | null;
  /** All values that are contained in given list. */
  width_in?: number[] | null;
  /** All values that are not contained in given list. */
  width_not_in?: number[] | null;
  /** All values less than the given value. */
  width_lt?: number | null;
  /** All values less than or equal the given value. */
  width_lte?: number | null;
  /** All values greater than the given value. */
  width_gt?: number | null;
  /** All values greater than or equal the given value. */
  width_gte?: number | null;

  size?: number | null;
  /** All values that are not equal to given value. */
  size_not?: number | null;
  /** All values that are contained in given list. */
  size_in?: number[] | null;
  /** All values that are not contained in given list. */
  size_not_in?: number[] | null;
  /** All values less than the given value. */
  size_lt?: number | null;
  /** All values less than or equal the given value. */
  size_lte?: number | null;
  /** All values greater than the given value. */
  size_gt?: number | null;
  /** All values greater than or equal the given value. */
  size_gte?: number | null;

  mimeType?: string | null;
  /** All values that are not equal to given value. */
  mimeType_not?: string | null;
  /** All values that are contained in given list. */
  mimeType_in?: string[] | null;
  /** All values that are not contained in given list. */
  mimeType_not_in?: string[] | null;
  /** All values less than the given value. */
  mimeType_lt?: string | null;
  /** All values less than or equal the given value. */
  mimeType_lte?: string | null;
  /** All values greater than the given value. */
  mimeType_gt?: string | null;
  /** All values greater than or equal the given value. */
  mimeType_gte?: string | null;
  /** All values containing the given string. */
  mimeType_contains?: string | null;
  /** All values not containing the given string. */
  mimeType_not_contains?: string | null;
  /** All values starting with the given string. */
  mimeType_starts_with?: string | null;
  /** All values not starting with the given string. */
  mimeType_not_starts_with?: string | null;
  /** All values ending with the given string. */
  mimeType_ends_with?: string | null;
  /** All values not ending with the given string. */
  mimeType_not_ends_with?: string | null;

  assetMarketingStory_every?: MarketingStoryWhereInput | null;

  assetMarketingStory_some?: MarketingStoryWhereInput | null;

  assetMarketingStory_none?: MarketingStoryWhereInput | null;
}
/** Transformations for Assets */
export interface AssetTransformationInput {
  image?: ImageTransformationInput | null;

  document?: DocumentTransformationInput | null;
  /** Pass `true` if you want to validate the passed transformation parameters */
  validateOptions?: boolean | null;
}
/** Transformations for Images */
export interface ImageTransformationInput {
  /** Resizes the image */
  resize?: ImageResizeInput | null;
}

export interface ImageResizeInput {
  /** The width in pixels to resize the image to. The value must be an integer from 1 to 10000. */
  width?: number | null;
  /** The height in pixels to resize the image to. The value must be an integer from 1 to 10000. */
  height?: number | null;
  /** The default value for the fit parameter is fit:clip. */
  fit?: ImageFit | null;
}
/** Transformations for Documents */
export interface DocumentTransformationInput {
  /** Changes the output for the file. */
  output?: DocumentOutputInput | null;
}

export interface DocumentOutputInput {
  /** Transforms a document into a desired file type.See this matrix for format support:PDF:	jpg, odp, ods, odt, png, svg, txt, and webpDOC:	docx, html, jpg, odt, pdf, png, svg, txt, and webpDOCX:	doc, html, jpg, odt, pdf, png, svg, txt, and webpODT:	doc, docx, html, jpg, pdf, png, svg, txt, and webpXLS:	jpg, pdf, ods, png, svg, xlsx, and webpXLSX:	jpg, pdf, ods, png, svg, xls, and webpODS:	jpg, pdf, png, xls, svg, xlsx, and webpPPT:	jpg, odp, pdf, png, svg, pptx, and webpPPTX:	jpg, odp, pdf, png, svg, ppt, and webpODP:	jpg, pdf, png, ppt, svg, pptx, and webpBMP:	jpg, odp, ods, odt, pdf, png, svg, and webpGIF:	jpg, odp, ods, odt, pdf, png, svg, and webpJPG:	jpg, odp, ods, odt, pdf, png, svg, and webpPNG:	jpg, odp, ods, odt, pdf, png, svg, and webpWEBP:	jpg, odp, ods, odt, pdf, png, svg, and webpTIFF:	jpg, odp, ods, odt, pdf, png, svg, and webpAI:	    jpg, odp, ods, odt, pdf, png, svg, and webpPSD:	jpg, odp, ods, odt, pdf, png, svg, and webpSVG:	jpg, odp, ods, odt, pdf, png, and webpHTML:	jpg, odt, pdf, svg, txt, and webpTXT:	jpg, html, odt, pdf, svg, and webp */
  format?: DocumentFileTypes | null;
}

export interface CampaignInput {
  source?: string | null;

  medium?: string | null;

  term?: string | null;

  content?: string | null;

  name?: string | null;
}

export interface OfferInput {
  firstName: string;

  lastName: string;

  age: number;

  address: string;

  postalNumber: string;

  city?: string | null;

  insuranceType: InsuranceType;

  squareMeters: number;

  personsInHousehold: number;

  previousInsurer?: string | null;
}

export interface SignInput {
  personalNumber: string;

  email: string;
}

export interface ChatResponseTextInput {
  globalId: string;

  body: ChatResponseBodyTextInput;
}

export interface ChatResponseBodyTextInput {
  text: string;
}

export interface ChatResponseSingleSelectInput {
  globalId: string;

  body: ChatResponseBodySingleSelectInput;
}

export interface ChatResponseBodySingleSelectInput {
  selectedValue: string;
}

export interface ChatResponseFileInput {
  globalId: string;

  body: ChatResponseBodyFileInput;
}

export interface ChatResponseBodyFileInput {
  key: string;

  mimeType: string;
}

export interface ChatResponseAudioInput {
  globalId: string;

  body: ChatResponseBodyAudioInput;
}

export interface ChatResponseBodyAudioInput {
  url: string;
}

export interface ColorWhereInput {
  /** Logical AND on all given filters. */
  AND?: ColorWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: ColorWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: ColorWhereInput[] | null;

  updatedAt?: DateTime | null;
  /** All values that are not equal to given value. */
  updatedAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  updatedAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  updatedAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  updatedAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  updatedAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  updatedAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  updatedAt_gte?: DateTime | null;

  createdAt?: DateTime | null;
  /** All values that are not equal to given value. */
  createdAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  createdAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  createdAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  createdAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  createdAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  createdAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  createdAt_gte?: DateTime | null;

  id?: string | null;
  /** All values that are not equal to given value. */
  id_not?: string | null;
  /** All values that are contained in given list. */
  id_in?: string[] | null;
  /** All values that are not contained in given list. */
  id_not_in?: string[] | null;
  /** All values less than the given value. */
  id_lt?: string | null;
  /** All values less than or equal the given value. */
  id_lte?: string | null;
  /** All values greater than the given value. */
  id_gt?: string | null;
  /** All values greater than or equal the given value. */
  id_gte?: string | null;
  /** All values containing the given string. */
  id_contains?: string | null;
  /** All values not containing the given string. */
  id_not_contains?: string | null;
  /** All values starting with the given string. */
  id_starts_with?: string | null;
  /** All values not starting with the given string. */
  id_not_starts_with?: string | null;
  /** All values ending with the given string. */
  id_ends_with?: string | null;
  /** All values not ending with the given string. */
  id_not_ends_with?: string | null;
}

export interface LocationWhereInput {
  /** Logical AND on all given filters. */
  AND?: LocationWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: LocationWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: LocationWhereInput[] | null;

  updatedAt?: DateTime | null;
  /** All values that are not equal to given value. */
  updatedAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  updatedAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  updatedAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  updatedAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  updatedAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  updatedAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  updatedAt_gte?: DateTime | null;

  createdAt?: DateTime | null;
  /** All values that are not equal to given value. */
  createdAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  createdAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  createdAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  createdAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  createdAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  createdAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  createdAt_gte?: DateTime | null;

  id?: string | null;
  /** All values that are not equal to given value. */
  id_not?: string | null;
  /** All values that are contained in given list. */
  id_in?: string[] | null;
  /** All values that are not contained in given list. */
  id_not_in?: string[] | null;
  /** All values less than the given value. */
  id_lt?: string | null;
  /** All values less than or equal the given value. */
  id_lte?: string | null;
  /** All values greater than the given value. */
  id_gt?: string | null;
  /** All values greater than or equal the given value. */
  id_gte?: string | null;
  /** All values containing the given string. */
  id_contains?: string | null;
  /** All values not containing the given string. */
  id_not_contains?: string | null;
  /** All values starting with the given string. */
  id_starts_with?: string | null;
  /** All values not starting with the given string. */
  id_not_starts_with?: string | null;
  /** All values ending with the given string. */
  id_ends_with?: string | null;
  /** All values not ending with the given string. */
  id_not_ends_with?: string | null;
}

export interface AssetWhereUniqueInput {
  id?: string | null;

  handle?: string | null;
}

export interface ColorWhereUniqueInput {
  id?: string | null;
}

export interface LocationWhereUniqueInput {
  id?: string | null;
}

export interface LanguageWhereUniqueInput {
  id?: string | null;

  code?: string | null;

  name?: string | null;
}

export interface KeyWhereUniqueInput {
  id?: string | null;

  value?: string | null;
}

export interface TranslationWhereUniqueInput {
  id?: string | null;
}

export interface PerilWhereUniqueInput {
  id?: string | null;
}

export interface PerilCategoryWhereUniqueInput {
  id?: string | null;

  name?: string | null;
}

export interface MarketingStoryWhereUniqueInput {
  id?: string | null;
}

export interface AssetCreateInput {
  status?: Status | null;

  handle: string;

  fileName: string;

  height?: number | null;

  width?: number | null;

  size?: number | null;

  mimeType?: string | null;

  assetMarketingStory?: MarketingStoryCreateManyWithoutAssetInput | null;
}

export interface MarketingStoryCreateManyWithoutAssetInput {
  create?: MarketingStoryCreateWithoutAssetInput[] | null;

  connect?: MarketingStoryWhereUniqueInput[] | null;
}

export interface MarketingStoryCreateWithoutAssetInput {
  status?: Status | null;

  duration?: number | null;

  importance?: number | null;

  backgroundColor: HedvigColor;
}

export interface LanguageCreateInput {
  status?: Status | null;

  code: string;

  name: string;

  translations?: TranslationCreateManyWithoutLanguageInput | null;
}

export interface TranslationCreateManyWithoutLanguageInput {
  create?: TranslationCreateWithoutLanguageInput[] | null;

  connect?: TranslationWhereUniqueInput[] | null;
}

export interface TranslationCreateWithoutLanguageInput {
  status?: Status | null;

  project?: Project | null;

  text: string;

  key?: KeyCreateOneWithoutTranslationsInput | null;
}

export interface KeyCreateOneWithoutTranslationsInput {
  create?: KeyCreateWithoutTranslationsInput | null;

  connect?: KeyWhereUniqueInput | null;
}

export interface KeyCreateWithoutTranslationsInput {
  status?: Status | null;

  value: string;

  description?: string | null;

  perilCategoryTitle?: PerilCategoryCreateOneWithoutTitleKeyInput | null;

  perilCategoryDescription?: PerilCategoryCreateOneWithoutDescriptionKeyInput | null;

  perilTitle?: PerilCreateOneWithoutTitleKeyInput | null;

  perilDescription?: PerilCreateOneWithoutDescriptionKeyInput | null;
}

export interface PerilCategoryCreateOneWithoutTitleKeyInput {
  create?: PerilCategoryCreateWithoutTitleKeyInput | null;

  connect?: PerilCategoryWhereUniqueInput | null;
}

export interface PerilCategoryCreateWithoutTitleKeyInput {
  status?: Status | null;

  name: string;

  perils?: PerilCreateManyWithoutPerilCategoryInput | null;

  descriptionKey?: KeyCreateOneWithoutPerilCategoryDescriptionInput | null;
}

export interface PerilCreateManyWithoutPerilCategoryInput {
  create?: PerilCreateWithoutPerilCategoryInput[] | null;

  connect?: PerilWhereUniqueInput[] | null;
}

export interface PerilCreateWithoutPerilCategoryInput {
  status?: Status | null;

  titleKey?: KeyCreateOneWithoutPerilTitleInput | null;

  descriptionKey?: KeyCreateOneWithoutPerilDescriptionInput | null;
}

export interface KeyCreateOneWithoutPerilTitleInput {
  create?: KeyCreateWithoutPerilTitleInput | null;

  connect?: KeyWhereUniqueInput | null;
}

export interface KeyCreateWithoutPerilTitleInput {
  status?: Status | null;

  value: string;

  description?: string | null;

  translations?: TranslationCreateManyWithoutKeyInput | null;

  perilCategoryTitle?: PerilCategoryCreateOneWithoutTitleKeyInput | null;

  perilCategoryDescription?: PerilCategoryCreateOneWithoutDescriptionKeyInput | null;

  perilDescription?: PerilCreateOneWithoutDescriptionKeyInput | null;
}

export interface TranslationCreateManyWithoutKeyInput {
  create?: TranslationCreateWithoutKeyInput[] | null;

  connect?: TranslationWhereUniqueInput[] | null;
}

export interface TranslationCreateWithoutKeyInput {
  status?: Status | null;

  project?: Project | null;

  text: string;

  language?: LanguageCreateOneWithoutTranslationsInput | null;
}

export interface LanguageCreateOneWithoutTranslationsInput {
  create?: LanguageCreateWithoutTranslationsInput | null;

  connect?: LanguageWhereUniqueInput | null;
}

export interface LanguageCreateWithoutTranslationsInput {
  status?: Status | null;

  code: string;

  name: string;
}

export interface PerilCategoryCreateOneWithoutDescriptionKeyInput {
  create?: PerilCategoryCreateWithoutDescriptionKeyInput | null;

  connect?: PerilCategoryWhereUniqueInput | null;
}

export interface PerilCategoryCreateWithoutDescriptionKeyInput {
  status?: Status | null;

  name: string;

  perils?: PerilCreateManyWithoutPerilCategoryInput | null;

  titleKey?: KeyCreateOneWithoutPerilCategoryTitleInput | null;
}

export interface KeyCreateOneWithoutPerilCategoryTitleInput {
  create?: KeyCreateWithoutPerilCategoryTitleInput | null;

  connect?: KeyWhereUniqueInput | null;
}

export interface KeyCreateWithoutPerilCategoryTitleInput {
  status?: Status | null;

  value: string;

  description?: string | null;

  translations?: TranslationCreateManyWithoutKeyInput | null;

  perilCategoryDescription?: PerilCategoryCreateOneWithoutDescriptionKeyInput | null;

  perilTitle?: PerilCreateOneWithoutTitleKeyInput | null;

  perilDescription?: PerilCreateOneWithoutDescriptionKeyInput | null;
}

export interface PerilCreateOneWithoutTitleKeyInput {
  create?: PerilCreateWithoutTitleKeyInput | null;

  connect?: PerilWhereUniqueInput | null;
}

export interface PerilCreateWithoutTitleKeyInput {
  status?: Status | null;

  perilCategory?: PerilCategoryCreateOneWithoutPerilsInput | null;

  descriptionKey?: KeyCreateOneWithoutPerilDescriptionInput | null;
}

export interface PerilCategoryCreateOneWithoutPerilsInput {
  create?: PerilCategoryCreateWithoutPerilsInput | null;

  connect?: PerilCategoryWhereUniqueInput | null;
}

export interface PerilCategoryCreateWithoutPerilsInput {
  status?: Status | null;

  name: string;

  titleKey?: KeyCreateOneWithoutPerilCategoryTitleInput | null;

  descriptionKey?: KeyCreateOneWithoutPerilCategoryDescriptionInput | null;
}

export interface KeyCreateOneWithoutPerilCategoryDescriptionInput {
  create?: KeyCreateWithoutPerilCategoryDescriptionInput | null;

  connect?: KeyWhereUniqueInput | null;
}

export interface KeyCreateWithoutPerilCategoryDescriptionInput {
  status?: Status | null;

  value: string;

  description?: string | null;

  translations?: TranslationCreateManyWithoutKeyInput | null;

  perilCategoryTitle?: PerilCategoryCreateOneWithoutTitleKeyInput | null;

  perilTitle?: PerilCreateOneWithoutTitleKeyInput | null;

  perilDescription?: PerilCreateOneWithoutDescriptionKeyInput | null;
}

export interface PerilCreateOneWithoutDescriptionKeyInput {
  create?: PerilCreateWithoutDescriptionKeyInput | null;

  connect?: PerilWhereUniqueInput | null;
}

export interface PerilCreateWithoutDescriptionKeyInput {
  status?: Status | null;

  perilCategory?: PerilCategoryCreateOneWithoutPerilsInput | null;

  titleKey?: KeyCreateOneWithoutPerilTitleInput | null;
}

export interface KeyCreateOneWithoutPerilDescriptionInput {
  create?: KeyCreateWithoutPerilDescriptionInput | null;

  connect?: KeyWhereUniqueInput | null;
}

export interface KeyCreateWithoutPerilDescriptionInput {
  status?: Status | null;

  value: string;

  description?: string | null;

  translations?: TranslationCreateManyWithoutKeyInput | null;

  perilCategoryTitle?: PerilCategoryCreateOneWithoutTitleKeyInput | null;

  perilCategoryDescription?: PerilCategoryCreateOneWithoutDescriptionKeyInput | null;

  perilTitle?: PerilCreateOneWithoutTitleKeyInput | null;
}

export interface KeyCreateInput {
  status?: Status | null;

  value: string;

  description?: string | null;

  translations?: TranslationCreateManyWithoutKeyInput | null;

  perilCategoryTitle?: PerilCategoryCreateOneWithoutTitleKeyInput | null;

  perilCategoryDescription?: PerilCategoryCreateOneWithoutDescriptionKeyInput | null;

  perilTitle?: PerilCreateOneWithoutTitleKeyInput | null;

  perilDescription?: PerilCreateOneWithoutDescriptionKeyInput | null;
}

export interface TranslationCreateInput {
  status?: Status | null;

  project?: Project | null;

  text: string;

  language?: LanguageCreateOneWithoutTranslationsInput | null;

  key?: KeyCreateOneWithoutTranslationsInput | null;
}

export interface PerilCreateInput {
  status?: Status | null;

  perilCategory?: PerilCategoryCreateOneWithoutPerilsInput | null;

  titleKey?: KeyCreateOneWithoutPerilTitleInput | null;

  descriptionKey?: KeyCreateOneWithoutPerilDescriptionInput | null;
}

export interface PerilCategoryCreateInput {
  status?: Status | null;

  name: string;

  perils?: PerilCreateManyWithoutPerilCategoryInput | null;

  titleKey?: KeyCreateOneWithoutPerilCategoryTitleInput | null;

  descriptionKey?: KeyCreateOneWithoutPerilCategoryDescriptionInput | null;
}

export interface MarketingStoryCreateInput {
  status?: Status | null;

  duration?: number | null;

  importance?: number | null;

  backgroundColor: HedvigColor;

  asset?: AssetCreateOneWithoutAssetMarketingStoryInput | null;
}

export interface AssetCreateOneWithoutAssetMarketingStoryInput {
  create?: AssetCreateWithoutAssetMarketingStoryInput | null;

  connect?: AssetWhereUniqueInput | null;
}

export interface AssetCreateWithoutAssetMarketingStoryInput {
  status?: Status | null;

  handle: string;

  fileName: string;

  height?: number | null;

  width?: number | null;

  size?: number | null;

  mimeType?: string | null;
}

export interface AssetUpdateInput {
  status?: Status | null;

  handle?: string | null;

  fileName?: string | null;

  height?: number | null;

  width?: number | null;

  size?: number | null;

  mimeType?: string | null;

  assetMarketingStory?: MarketingStoryUpdateManyWithoutAssetInput | null;
}

export interface MarketingStoryUpdateManyWithoutAssetInput {
  create?: MarketingStoryCreateWithoutAssetInput[] | null;

  connect?: MarketingStoryWhereUniqueInput[] | null;

  set?: MarketingStoryWhereUniqueInput[] | null;

  disconnect?: MarketingStoryWhereUniqueInput[] | null;

  delete?: MarketingStoryWhereUniqueInput[] | null;

  update?: MarketingStoryUpdateWithWhereUniqueWithoutAssetInput[] | null;

  updateMany?: MarketingStoryUpdateManyWithWhereNestedInput[] | null;

  deleteMany?: MarketingStoryScalarWhereInput[] | null;

  upsert?: MarketingStoryUpsertWithWhereUniqueWithoutAssetInput[] | null;
}

export interface MarketingStoryUpdateWithWhereUniqueWithoutAssetInput {
  where: MarketingStoryWhereUniqueInput;

  data: MarketingStoryUpdateWithoutAssetDataInput;
}

export interface MarketingStoryUpdateWithoutAssetDataInput {
  status?: Status | null;

  duration?: number | null;

  importance?: number | null;

  backgroundColor?: HedvigColor | null;
}

export interface MarketingStoryUpdateManyWithWhereNestedInput {
  where: MarketingStoryScalarWhereInput;

  data: MarketingStoryUpdateManyDataInput;
}

export interface MarketingStoryScalarWhereInput {
  /** Logical AND on all given filters. */
  AND?: MarketingStoryScalarWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: MarketingStoryScalarWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: MarketingStoryScalarWhereInput[] | null;

  status?: Status | null;
  /** All values that are not equal to given value. */
  status_not?: Status | null;
  /** All values that are contained in given list. */
  status_in?: Status[] | null;
  /** All values that are not contained in given list. */
  status_not_in?: Status[] | null;

  updatedAt?: DateTime | null;
  /** All values that are not equal to given value. */
  updatedAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  updatedAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  updatedAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  updatedAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  updatedAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  updatedAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  updatedAt_gte?: DateTime | null;

  createdAt?: DateTime | null;
  /** All values that are not equal to given value. */
  createdAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  createdAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  createdAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  createdAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  createdAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  createdAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  createdAt_gte?: DateTime | null;

  id?: string | null;
  /** All values that are not equal to given value. */
  id_not?: string | null;
  /** All values that are contained in given list. */
  id_in?: string[] | null;
  /** All values that are not contained in given list. */
  id_not_in?: string[] | null;
  /** All values less than the given value. */
  id_lt?: string | null;
  /** All values less than or equal the given value. */
  id_lte?: string | null;
  /** All values greater than the given value. */
  id_gt?: string | null;
  /** All values greater than or equal the given value. */
  id_gte?: string | null;
  /** All values containing the given string. */
  id_contains?: string | null;
  /** All values not containing the given string. */
  id_not_contains?: string | null;
  /** All values starting with the given string. */
  id_starts_with?: string | null;
  /** All values not starting with the given string. */
  id_not_starts_with?: string | null;
  /** All values ending with the given string. */
  id_ends_with?: string | null;
  /** All values not ending with the given string. */
  id_not_ends_with?: string | null;

  duration?: number | null;
  /** All values that are not equal to given value. */
  duration_not?: number | null;
  /** All values that are contained in given list. */
  duration_in?: number[] | null;
  /** All values that are not contained in given list. */
  duration_not_in?: number[] | null;
  /** All values less than the given value. */
  duration_lt?: number | null;
  /** All values less than or equal the given value. */
  duration_lte?: number | null;
  /** All values greater than the given value. */
  duration_gt?: number | null;
  /** All values greater than or equal the given value. */
  duration_gte?: number | null;

  importance?: number | null;
  /** All values that are not equal to given value. */
  importance_not?: number | null;
  /** All values that are contained in given list. */
  importance_in?: number[] | null;
  /** All values that are not contained in given list. */
  importance_not_in?: number[] | null;
  /** All values less than the given value. */
  importance_lt?: number | null;
  /** All values less than or equal the given value. */
  importance_lte?: number | null;
  /** All values greater than the given value. */
  importance_gt?: number | null;
  /** All values greater than or equal the given value. */
  importance_gte?: number | null;

  backgroundColor?: HedvigColor | null;
  /** All values that are not equal to given value. */
  backgroundColor_not?: HedvigColor | null;
  /** All values that are contained in given list. */
  backgroundColor_in?: HedvigColor[] | null;
  /** All values that are not contained in given list. */
  backgroundColor_not_in?: HedvigColor[] | null;
}

export interface MarketingStoryUpdateManyDataInput {
  status?: Status | null;

  duration?: number | null;

  importance?: number | null;

  backgroundColor?: HedvigColor | null;
}

export interface MarketingStoryUpsertWithWhereUniqueWithoutAssetInput {
  where: MarketingStoryWhereUniqueInput;

  update: MarketingStoryUpdateWithoutAssetDataInput;

  create: MarketingStoryCreateWithoutAssetInput;
}

export interface LanguageUpdateInput {
  status?: Status | null;

  code?: string | null;

  name?: string | null;

  translations?: TranslationUpdateManyWithoutLanguageInput | null;
}

export interface TranslationUpdateManyWithoutLanguageInput {
  create?: TranslationCreateWithoutLanguageInput[] | null;

  connect?: TranslationWhereUniqueInput[] | null;

  set?: TranslationWhereUniqueInput[] | null;

  disconnect?: TranslationWhereUniqueInput[] | null;

  delete?: TranslationWhereUniqueInput[] | null;

  update?: TranslationUpdateWithWhereUniqueWithoutLanguageInput[] | null;

  updateMany?: TranslationUpdateManyWithWhereNestedInput[] | null;

  deleteMany?: TranslationScalarWhereInput[] | null;

  upsert?: TranslationUpsertWithWhereUniqueWithoutLanguageInput[] | null;
}

export interface TranslationUpdateWithWhereUniqueWithoutLanguageInput {
  where: TranslationWhereUniqueInput;

  data: TranslationUpdateWithoutLanguageDataInput;
}

export interface TranslationUpdateWithoutLanguageDataInput {
  status?: Status | null;

  project?: Project | null;

  text?: string | null;

  key?: KeyUpdateOneWithoutTranslationsInput | null;
}

export interface KeyUpdateOneWithoutTranslationsInput {
  create?: KeyCreateWithoutTranslationsInput | null;

  connect?: KeyWhereUniqueInput | null;

  disconnect?: boolean | null;

  delete?: boolean | null;

  update?: KeyUpdateWithoutTranslationsDataInput | null;

  upsert?: KeyUpsertWithoutTranslationsInput | null;
}

export interface KeyUpdateWithoutTranslationsDataInput {
  status?: Status | null;

  value?: string | null;

  description?: string | null;

  perilCategoryTitle?: PerilCategoryUpdateOneWithoutTitleKeyInput | null;

  perilCategoryDescription?: PerilCategoryUpdateOneWithoutDescriptionKeyInput | null;

  perilTitle?: PerilUpdateOneWithoutTitleKeyInput | null;

  perilDescription?: PerilUpdateOneWithoutDescriptionKeyInput | null;
}

export interface PerilCategoryUpdateOneWithoutTitleKeyInput {
  create?: PerilCategoryCreateWithoutTitleKeyInput | null;

  connect?: PerilCategoryWhereUniqueInput | null;

  disconnect?: boolean | null;

  delete?: boolean | null;

  update?: PerilCategoryUpdateWithoutTitleKeyDataInput | null;

  upsert?: PerilCategoryUpsertWithoutTitleKeyInput | null;
}

export interface PerilCategoryUpdateWithoutTitleKeyDataInput {
  status?: Status | null;

  name?: string | null;

  perils?: PerilUpdateManyWithoutPerilCategoryInput | null;

  descriptionKey?: KeyUpdateOneWithoutPerilCategoryDescriptionInput | null;
}

export interface PerilUpdateManyWithoutPerilCategoryInput {
  create?: PerilCreateWithoutPerilCategoryInput[] | null;

  connect?: PerilWhereUniqueInput[] | null;

  set?: PerilWhereUniqueInput[] | null;

  disconnect?: PerilWhereUniqueInput[] | null;

  delete?: PerilWhereUniqueInput[] | null;

  update?: PerilUpdateWithWhereUniqueWithoutPerilCategoryInput[] | null;

  updateMany?: PerilUpdateManyWithWhereNestedInput[] | null;

  deleteMany?: PerilScalarWhereInput[] | null;

  upsert?: PerilUpsertWithWhereUniqueWithoutPerilCategoryInput[] | null;
}

export interface PerilUpdateWithWhereUniqueWithoutPerilCategoryInput {
  where: PerilWhereUniqueInput;

  data: PerilUpdateWithoutPerilCategoryDataInput;
}

export interface PerilUpdateWithoutPerilCategoryDataInput {
  status?: Status | null;

  titleKey?: KeyUpdateOneWithoutPerilTitleInput | null;

  descriptionKey?: KeyUpdateOneWithoutPerilDescriptionInput | null;
}

export interface KeyUpdateOneWithoutPerilTitleInput {
  create?: KeyCreateWithoutPerilTitleInput | null;

  connect?: KeyWhereUniqueInput | null;

  disconnect?: boolean | null;

  delete?: boolean | null;

  update?: KeyUpdateWithoutPerilTitleDataInput | null;

  upsert?: KeyUpsertWithoutPerilTitleInput | null;
}

export interface KeyUpdateWithoutPerilTitleDataInput {
  status?: Status | null;

  value?: string | null;

  description?: string | null;

  translations?: TranslationUpdateManyWithoutKeyInput | null;

  perilCategoryTitle?: PerilCategoryUpdateOneWithoutTitleKeyInput | null;

  perilCategoryDescription?: PerilCategoryUpdateOneWithoutDescriptionKeyInput | null;

  perilDescription?: PerilUpdateOneWithoutDescriptionKeyInput | null;
}

export interface TranslationUpdateManyWithoutKeyInput {
  create?: TranslationCreateWithoutKeyInput[] | null;

  connect?: TranslationWhereUniqueInput[] | null;

  set?: TranslationWhereUniqueInput[] | null;

  disconnect?: TranslationWhereUniqueInput[] | null;

  delete?: TranslationWhereUniqueInput[] | null;

  update?: TranslationUpdateWithWhereUniqueWithoutKeyInput[] | null;

  updateMany?: TranslationUpdateManyWithWhereNestedInput[] | null;

  deleteMany?: TranslationScalarWhereInput[] | null;

  upsert?: TranslationUpsertWithWhereUniqueWithoutKeyInput[] | null;
}

export interface TranslationUpdateWithWhereUniqueWithoutKeyInput {
  where: TranslationWhereUniqueInput;

  data: TranslationUpdateWithoutKeyDataInput;
}

export interface TranslationUpdateWithoutKeyDataInput {
  status?: Status | null;

  project?: Project | null;

  text?: string | null;

  language?: LanguageUpdateOneWithoutTranslationsInput | null;
}

export interface LanguageUpdateOneWithoutTranslationsInput {
  create?: LanguageCreateWithoutTranslationsInput | null;

  connect?: LanguageWhereUniqueInput | null;

  disconnect?: boolean | null;

  delete?: boolean | null;

  update?: LanguageUpdateWithoutTranslationsDataInput | null;

  upsert?: LanguageUpsertWithoutTranslationsInput | null;
}

export interface LanguageUpdateWithoutTranslationsDataInput {
  status?: Status | null;

  code?: string | null;

  name?: string | null;
}

export interface LanguageUpsertWithoutTranslationsInput {
  update: LanguageUpdateWithoutTranslationsDataInput;

  create: LanguageCreateWithoutTranslationsInput;
}

export interface TranslationUpdateManyWithWhereNestedInput {
  where: TranslationScalarWhereInput;

  data: TranslationUpdateManyDataInput;
}

export interface TranslationScalarWhereInput {
  /** Logical AND on all given filters. */
  AND?: TranslationScalarWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: TranslationScalarWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: TranslationScalarWhereInput[] | null;

  status?: Status | null;
  /** All values that are not equal to given value. */
  status_not?: Status | null;
  /** All values that are contained in given list. */
  status_in?: Status[] | null;
  /** All values that are not contained in given list. */
  status_not_in?: Status[] | null;

  updatedAt?: DateTime | null;
  /** All values that are not equal to given value. */
  updatedAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  updatedAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  updatedAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  updatedAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  updatedAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  updatedAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  updatedAt_gte?: DateTime | null;

  createdAt?: DateTime | null;
  /** All values that are not equal to given value. */
  createdAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  createdAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  createdAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  createdAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  createdAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  createdAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  createdAt_gte?: DateTime | null;

  id?: string | null;
  /** All values that are not equal to given value. */
  id_not?: string | null;
  /** All values that are contained in given list. */
  id_in?: string[] | null;
  /** All values that are not contained in given list. */
  id_not_in?: string[] | null;
  /** All values less than the given value. */
  id_lt?: string | null;
  /** All values less than or equal the given value. */
  id_lte?: string | null;
  /** All values greater than the given value. */
  id_gt?: string | null;
  /** All values greater than or equal the given value. */
  id_gte?: string | null;
  /** All values containing the given string. */
  id_contains?: string | null;
  /** All values not containing the given string. */
  id_not_contains?: string | null;
  /** All values starting with the given string. */
  id_starts_with?: string | null;
  /** All values not starting with the given string. */
  id_not_starts_with?: string | null;
  /** All values ending with the given string. */
  id_ends_with?: string | null;
  /** All values not ending with the given string. */
  id_not_ends_with?: string | null;

  project?: Project | null;
  /** All values that are not equal to given value. */
  project_not?: Project | null;
  /** All values that are contained in given list. */
  project_in?: Project[] | null;
  /** All values that are not contained in given list. */
  project_not_in?: Project[] | null;

  text?: string | null;
  /** All values that are not equal to given value. */
  text_not?: string | null;
  /** All values that are contained in given list. */
  text_in?: string[] | null;
  /** All values that are not contained in given list. */
  text_not_in?: string[] | null;
  /** All values less than the given value. */
  text_lt?: string | null;
  /** All values less than or equal the given value. */
  text_lte?: string | null;
  /** All values greater than the given value. */
  text_gt?: string | null;
  /** All values greater than or equal the given value. */
  text_gte?: string | null;
  /** All values containing the given string. */
  text_contains?: string | null;
  /** All values not containing the given string. */
  text_not_contains?: string | null;
  /** All values starting with the given string. */
  text_starts_with?: string | null;
  /** All values not starting with the given string. */
  text_not_starts_with?: string | null;
  /** All values ending with the given string. */
  text_ends_with?: string | null;
  /** All values not ending with the given string. */
  text_not_ends_with?: string | null;
}

export interface TranslationUpdateManyDataInput {
  status?: Status | null;

  project?: Project | null;

  text?: string | null;
}

export interface TranslationUpsertWithWhereUniqueWithoutKeyInput {
  where: TranslationWhereUniqueInput;

  update: TranslationUpdateWithoutKeyDataInput;

  create: TranslationCreateWithoutKeyInput;
}

export interface PerilCategoryUpdateOneWithoutDescriptionKeyInput {
  create?: PerilCategoryCreateWithoutDescriptionKeyInput | null;

  connect?: PerilCategoryWhereUniqueInput | null;

  disconnect?: boolean | null;

  delete?: boolean | null;

  update?: PerilCategoryUpdateWithoutDescriptionKeyDataInput | null;

  upsert?: PerilCategoryUpsertWithoutDescriptionKeyInput | null;
}

export interface PerilCategoryUpdateWithoutDescriptionKeyDataInput {
  status?: Status | null;

  name?: string | null;

  perils?: PerilUpdateManyWithoutPerilCategoryInput | null;

  titleKey?: KeyUpdateOneWithoutPerilCategoryTitleInput | null;
}

export interface KeyUpdateOneWithoutPerilCategoryTitleInput {
  create?: KeyCreateWithoutPerilCategoryTitleInput | null;

  connect?: KeyWhereUniqueInput | null;

  disconnect?: boolean | null;

  delete?: boolean | null;

  update?: KeyUpdateWithoutPerilCategoryTitleDataInput | null;

  upsert?: KeyUpsertWithoutPerilCategoryTitleInput | null;
}

export interface KeyUpdateWithoutPerilCategoryTitleDataInput {
  status?: Status | null;

  value?: string | null;

  description?: string | null;

  translations?: TranslationUpdateManyWithoutKeyInput | null;

  perilCategoryDescription?: PerilCategoryUpdateOneWithoutDescriptionKeyInput | null;

  perilTitle?: PerilUpdateOneWithoutTitleKeyInput | null;

  perilDescription?: PerilUpdateOneWithoutDescriptionKeyInput | null;
}

export interface PerilUpdateOneWithoutTitleKeyInput {
  create?: PerilCreateWithoutTitleKeyInput | null;

  connect?: PerilWhereUniqueInput | null;

  disconnect?: boolean | null;

  delete?: boolean | null;

  update?: PerilUpdateWithoutTitleKeyDataInput | null;

  upsert?: PerilUpsertWithoutTitleKeyInput | null;
}

export interface PerilUpdateWithoutTitleKeyDataInput {
  status?: Status | null;

  perilCategory?: PerilCategoryUpdateOneWithoutPerilsInput | null;

  descriptionKey?: KeyUpdateOneWithoutPerilDescriptionInput | null;
}

export interface PerilCategoryUpdateOneWithoutPerilsInput {
  create?: PerilCategoryCreateWithoutPerilsInput | null;

  connect?: PerilCategoryWhereUniqueInput | null;

  disconnect?: boolean | null;

  delete?: boolean | null;

  update?: PerilCategoryUpdateWithoutPerilsDataInput | null;

  upsert?: PerilCategoryUpsertWithoutPerilsInput | null;
}

export interface PerilCategoryUpdateWithoutPerilsDataInput {
  status?: Status | null;

  name?: string | null;

  titleKey?: KeyUpdateOneWithoutPerilCategoryTitleInput | null;

  descriptionKey?: KeyUpdateOneWithoutPerilCategoryDescriptionInput | null;
}

export interface KeyUpdateOneWithoutPerilCategoryDescriptionInput {
  create?: KeyCreateWithoutPerilCategoryDescriptionInput | null;

  connect?: KeyWhereUniqueInput | null;

  disconnect?: boolean | null;

  delete?: boolean | null;

  update?: KeyUpdateWithoutPerilCategoryDescriptionDataInput | null;

  upsert?: KeyUpsertWithoutPerilCategoryDescriptionInput | null;
}

export interface KeyUpdateWithoutPerilCategoryDescriptionDataInput {
  status?: Status | null;

  value?: string | null;

  description?: string | null;

  translations?: TranslationUpdateManyWithoutKeyInput | null;

  perilCategoryTitle?: PerilCategoryUpdateOneWithoutTitleKeyInput | null;

  perilTitle?: PerilUpdateOneWithoutTitleKeyInput | null;

  perilDescription?: PerilUpdateOneWithoutDescriptionKeyInput | null;
}

export interface PerilUpdateOneWithoutDescriptionKeyInput {
  create?: PerilCreateWithoutDescriptionKeyInput | null;

  connect?: PerilWhereUniqueInput | null;

  disconnect?: boolean | null;

  delete?: boolean | null;

  update?: PerilUpdateWithoutDescriptionKeyDataInput | null;

  upsert?: PerilUpsertWithoutDescriptionKeyInput | null;
}

export interface PerilUpdateWithoutDescriptionKeyDataInput {
  status?: Status | null;

  perilCategory?: PerilCategoryUpdateOneWithoutPerilsInput | null;

  titleKey?: KeyUpdateOneWithoutPerilTitleInput | null;
}

export interface PerilUpsertWithoutDescriptionKeyInput {
  update: PerilUpdateWithoutDescriptionKeyDataInput;

  create: PerilCreateWithoutDescriptionKeyInput;
}

export interface KeyUpsertWithoutPerilCategoryDescriptionInput {
  update: KeyUpdateWithoutPerilCategoryDescriptionDataInput;

  create: KeyCreateWithoutPerilCategoryDescriptionInput;
}

export interface PerilCategoryUpsertWithoutPerilsInput {
  update: PerilCategoryUpdateWithoutPerilsDataInput;

  create: PerilCategoryCreateWithoutPerilsInput;
}

export interface KeyUpdateOneWithoutPerilDescriptionInput {
  create?: KeyCreateWithoutPerilDescriptionInput | null;

  connect?: KeyWhereUniqueInput | null;

  disconnect?: boolean | null;

  delete?: boolean | null;

  update?: KeyUpdateWithoutPerilDescriptionDataInput | null;

  upsert?: KeyUpsertWithoutPerilDescriptionInput | null;
}

export interface KeyUpdateWithoutPerilDescriptionDataInput {
  status?: Status | null;

  value?: string | null;

  description?: string | null;

  translations?: TranslationUpdateManyWithoutKeyInput | null;

  perilCategoryTitle?: PerilCategoryUpdateOneWithoutTitleKeyInput | null;

  perilCategoryDescription?: PerilCategoryUpdateOneWithoutDescriptionKeyInput | null;

  perilTitle?: PerilUpdateOneWithoutTitleKeyInput | null;
}

export interface KeyUpsertWithoutPerilDescriptionInput {
  update: KeyUpdateWithoutPerilDescriptionDataInput;

  create: KeyCreateWithoutPerilDescriptionInput;
}

export interface PerilUpsertWithoutTitleKeyInput {
  update: PerilUpdateWithoutTitleKeyDataInput;

  create: PerilCreateWithoutTitleKeyInput;
}

export interface KeyUpsertWithoutPerilCategoryTitleInput {
  update: KeyUpdateWithoutPerilCategoryTitleDataInput;

  create: KeyCreateWithoutPerilCategoryTitleInput;
}

export interface PerilCategoryUpsertWithoutDescriptionKeyInput {
  update: PerilCategoryUpdateWithoutDescriptionKeyDataInput;

  create: PerilCategoryCreateWithoutDescriptionKeyInput;
}

export interface KeyUpsertWithoutPerilTitleInput {
  update: KeyUpdateWithoutPerilTitleDataInput;

  create: KeyCreateWithoutPerilTitleInput;
}

export interface PerilUpdateManyWithWhereNestedInput {
  where: PerilScalarWhereInput;

  data: PerilUpdateManyDataInput;
}

export interface PerilScalarWhereInput {
  /** Logical AND on all given filters. */
  AND?: PerilScalarWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: PerilScalarWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: PerilScalarWhereInput[] | null;

  status?: Status | null;
  /** All values that are not equal to given value. */
  status_not?: Status | null;
  /** All values that are contained in given list. */
  status_in?: Status[] | null;
  /** All values that are not contained in given list. */
  status_not_in?: Status[] | null;

  updatedAt?: DateTime | null;
  /** All values that are not equal to given value. */
  updatedAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  updatedAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  updatedAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  updatedAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  updatedAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  updatedAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  updatedAt_gte?: DateTime | null;

  createdAt?: DateTime | null;
  /** All values that are not equal to given value. */
  createdAt_not?: DateTime | null;
  /** All values that are contained in given list. */
  createdAt_in?: DateTime[] | null;
  /** All values that are not contained in given list. */
  createdAt_not_in?: DateTime[] | null;
  /** All values less than the given value. */
  createdAt_lt?: DateTime | null;
  /** All values less than or equal the given value. */
  createdAt_lte?: DateTime | null;
  /** All values greater than the given value. */
  createdAt_gt?: DateTime | null;
  /** All values greater than or equal the given value. */
  createdAt_gte?: DateTime | null;

  id?: string | null;
  /** All values that are not equal to given value. */
  id_not?: string | null;
  /** All values that are contained in given list. */
  id_in?: string[] | null;
  /** All values that are not contained in given list. */
  id_not_in?: string[] | null;
  /** All values less than the given value. */
  id_lt?: string | null;
  /** All values less than or equal the given value. */
  id_lte?: string | null;
  /** All values greater than the given value. */
  id_gt?: string | null;
  /** All values greater than or equal the given value. */
  id_gte?: string | null;
  /** All values containing the given string. */
  id_contains?: string | null;
  /** All values not containing the given string. */
  id_not_contains?: string | null;
  /** All values starting with the given string. */
  id_starts_with?: string | null;
  /** All values not starting with the given string. */
  id_not_starts_with?: string | null;
  /** All values ending with the given string. */
  id_ends_with?: string | null;
  /** All values not ending with the given string. */
  id_not_ends_with?: string | null;
}

export interface PerilUpdateManyDataInput {
  status?: Status | null;
}

export interface PerilUpsertWithWhereUniqueWithoutPerilCategoryInput {
  where: PerilWhereUniqueInput;

  update: PerilUpdateWithoutPerilCategoryDataInput;

  create: PerilCreateWithoutPerilCategoryInput;
}

export interface PerilCategoryUpsertWithoutTitleKeyInput {
  update: PerilCategoryUpdateWithoutTitleKeyDataInput;

  create: PerilCategoryCreateWithoutTitleKeyInput;
}

export interface KeyUpsertWithoutTranslationsInput {
  update: KeyUpdateWithoutTranslationsDataInput;

  create: KeyCreateWithoutTranslationsInput;
}

export interface TranslationUpsertWithWhereUniqueWithoutLanguageInput {
  where: TranslationWhereUniqueInput;

  update: TranslationUpdateWithoutLanguageDataInput;

  create: TranslationCreateWithoutLanguageInput;
}

export interface KeyUpdateInput {
  status?: Status | null;

  value?: string | null;

  description?: string | null;

  translations?: TranslationUpdateManyWithoutKeyInput | null;

  perilCategoryTitle?: PerilCategoryUpdateOneWithoutTitleKeyInput | null;

  perilCategoryDescription?: PerilCategoryUpdateOneWithoutDescriptionKeyInput | null;

  perilTitle?: PerilUpdateOneWithoutTitleKeyInput | null;

  perilDescription?: PerilUpdateOneWithoutDescriptionKeyInput | null;
}

export interface TranslationUpdateInput {
  status?: Status | null;

  project?: Project | null;

  text?: string | null;

  language?: LanguageUpdateOneWithoutTranslationsInput | null;

  key?: KeyUpdateOneWithoutTranslationsInput | null;
}

export interface PerilUpdateInput {
  status?: Status | null;

  perilCategory?: PerilCategoryUpdateOneWithoutPerilsInput | null;

  titleKey?: KeyUpdateOneWithoutPerilTitleInput | null;

  descriptionKey?: KeyUpdateOneWithoutPerilDescriptionInput | null;
}

export interface PerilCategoryUpdateInput {
  status?: Status | null;

  name?: string | null;

  perils?: PerilUpdateManyWithoutPerilCategoryInput | null;

  titleKey?: KeyUpdateOneWithoutPerilCategoryTitleInput | null;

  descriptionKey?: KeyUpdateOneWithoutPerilCategoryDescriptionInput | null;
}

export interface MarketingStoryUpdateInput {
  status?: Status | null;

  duration?: number | null;

  importance?: number | null;

  backgroundColor?: HedvigColor | null;

  asset?: AssetUpdateOneWithoutAssetMarketingStoryInput | null;
}

export interface AssetUpdateOneWithoutAssetMarketingStoryInput {
  create?: AssetCreateWithoutAssetMarketingStoryInput | null;

  connect?: AssetWhereUniqueInput | null;

  disconnect?: boolean | null;

  delete?: boolean | null;

  update?: AssetUpdateWithoutAssetMarketingStoryDataInput | null;

  upsert?: AssetUpsertWithoutAssetMarketingStoryInput | null;
}

export interface AssetUpdateWithoutAssetMarketingStoryDataInput {
  status?: Status | null;

  handle?: string | null;

  fileName?: string | null;

  height?: number | null;

  width?: number | null;

  size?: number | null;

  mimeType?: string | null;
}

export interface AssetUpsertWithoutAssetMarketingStoryInput {
  update: AssetUpdateWithoutAssetMarketingStoryDataInput;

  create: AssetCreateWithoutAssetMarketingStoryInput;
}

export interface AssetUpdateManyMutationInput {
  status?: Status | null;

  handle?: string | null;

  fileName?: string | null;

  height?: number | null;

  width?: number | null;

  size?: number | null;

  mimeType?: string | null;
}

export interface LanguageUpdateManyMutationInput {
  status?: Status | null;

  code?: string | null;

  name?: string | null;
}

export interface KeyUpdateManyMutationInput {
  status?: Status | null;

  value?: string | null;

  description?: string | null;
}

export interface TranslationUpdateManyMutationInput {
  status?: Status | null;

  project?: Project | null;

  text?: string | null;
}

export interface PerilUpdateManyMutationInput {
  status?: Status | null;
}

export interface PerilCategoryUpdateManyMutationInput {
  status?: Status | null;

  name?: string | null;
}

export interface MarketingStoryUpdateManyMutationInput {
  status?: Status | null;

  duration?: number | null;

  importance?: number | null;

  backgroundColor?: HedvigColor | null;
}

export interface AssetSubscriptionWhereInput {
  /** Logical AND on all given filters. */
  AND?: AssetSubscriptionWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: AssetSubscriptionWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: AssetSubscriptionWhereInput[] | null;
  /** The subscription event gets dispatched when it's listed in mutation_in */
  mutation_in?: MutationType[] | null;
  /** The subscription event gets only dispatched when one of the updated fields names is included in this list */
  updatedFields_contains?: string | null;
  /** The subscription event gets only dispatched when all of the field names included in this list have been updated */
  updatedFields_contains_every?: string[] | null;
  /** The subscription event gets only dispatched when some of the field names included in this list have been updated */
  updatedFields_contains_some?: string[] | null;

  node?: AssetWhereInput | null;
}

export interface ColorSubscriptionWhereInput {
  /** Logical AND on all given filters. */
  AND?: ColorSubscriptionWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: ColorSubscriptionWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: ColorSubscriptionWhereInput[] | null;
  /** The subscription event gets dispatched when it's listed in mutation_in */
  mutation_in?: MutationType[] | null;
  /** The subscription event gets only dispatched when one of the updated fields names is included in this list */
  updatedFields_contains?: string | null;
  /** The subscription event gets only dispatched when all of the field names included in this list have been updated */
  updatedFields_contains_every?: string[] | null;
  /** The subscription event gets only dispatched when some of the field names included in this list have been updated */
  updatedFields_contains_some?: string[] | null;

  node?: ColorWhereInput | null;
}

export interface KeySubscriptionWhereInput {
  /** Logical AND on all given filters. */
  AND?: KeySubscriptionWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: KeySubscriptionWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: KeySubscriptionWhereInput[] | null;
  /** The subscription event gets dispatched when it's listed in mutation_in */
  mutation_in?: MutationType[] | null;
  /** The subscription event gets only dispatched when one of the updated fields names is included in this list */
  updatedFields_contains?: string | null;
  /** The subscription event gets only dispatched when all of the field names included in this list have been updated */
  updatedFields_contains_every?: string[] | null;
  /** The subscription event gets only dispatched when some of the field names included in this list have been updated */
  updatedFields_contains_some?: string[] | null;

  node?: KeyWhereInput | null;
}

export interface LanguageSubscriptionWhereInput {
  /** Logical AND on all given filters. */
  AND?: LanguageSubscriptionWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: LanguageSubscriptionWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: LanguageSubscriptionWhereInput[] | null;
  /** The subscription event gets dispatched when it's listed in mutation_in */
  mutation_in?: MutationType[] | null;
  /** The subscription event gets only dispatched when one of the updated fields names is included in this list */
  updatedFields_contains?: string | null;
  /** The subscription event gets only dispatched when all of the field names included in this list have been updated */
  updatedFields_contains_every?: string[] | null;
  /** The subscription event gets only dispatched when some of the field names included in this list have been updated */
  updatedFields_contains_some?: string[] | null;

  node?: LanguageWhereInput | null;
}

export interface LocationSubscriptionWhereInput {
  /** Logical AND on all given filters. */
  AND?: LocationSubscriptionWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: LocationSubscriptionWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: LocationSubscriptionWhereInput[] | null;
  /** The subscription event gets dispatched when it's listed in mutation_in */
  mutation_in?: MutationType[] | null;
  /** The subscription event gets only dispatched when one of the updated fields names is included in this list */
  updatedFields_contains?: string | null;
  /** The subscription event gets only dispatched when all of the field names included in this list have been updated */
  updatedFields_contains_every?: string[] | null;
  /** The subscription event gets only dispatched when some of the field names included in this list have been updated */
  updatedFields_contains_some?: string[] | null;

  node?: LocationWhereInput | null;
}

export interface MarketingStorySubscriptionWhereInput {
  /** Logical AND on all given filters. */
  AND?: MarketingStorySubscriptionWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: MarketingStorySubscriptionWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: MarketingStorySubscriptionWhereInput[] | null;
  /** The subscription event gets dispatched when it's listed in mutation_in */
  mutation_in?: MutationType[] | null;
  /** The subscription event gets only dispatched when one of the updated fields names is included in this list */
  updatedFields_contains?: string | null;
  /** The subscription event gets only dispatched when all of the field names included in this list have been updated */
  updatedFields_contains_every?: string[] | null;
  /** The subscription event gets only dispatched when some of the field names included in this list have been updated */
  updatedFields_contains_some?: string[] | null;

  node?: MarketingStoryWhereInput | null;
}

export interface PerilCategorySubscriptionWhereInput {
  /** Logical AND on all given filters. */
  AND?: PerilCategorySubscriptionWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: PerilCategorySubscriptionWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: PerilCategorySubscriptionWhereInput[] | null;
  /** The subscription event gets dispatched when it's listed in mutation_in */
  mutation_in?: MutationType[] | null;
  /** The subscription event gets only dispatched when one of the updated fields names is included in this list */
  updatedFields_contains?: string | null;
  /** The subscription event gets only dispatched when all of the field names included in this list have been updated */
  updatedFields_contains_every?: string[] | null;
  /** The subscription event gets only dispatched when some of the field names included in this list have been updated */
  updatedFields_contains_some?: string[] | null;

  node?: PerilCategoryWhereInput | null;
}

export interface PerilSubscriptionWhereInput {
  /** Logical AND on all given filters. */
  AND?: PerilSubscriptionWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: PerilSubscriptionWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: PerilSubscriptionWhereInput[] | null;
  /** The subscription event gets dispatched when it's listed in mutation_in */
  mutation_in?: MutationType[] | null;
  /** The subscription event gets only dispatched when one of the updated fields names is included in this list */
  updatedFields_contains?: string | null;
  /** The subscription event gets only dispatched when all of the field names included in this list have been updated */
  updatedFields_contains_every?: string[] | null;
  /** The subscription event gets only dispatched when some of the field names included in this list have been updated */
  updatedFields_contains_some?: string[] | null;

  node?: PerilWhereInput | null;
}

export interface TranslationSubscriptionWhereInput {
  /** Logical AND on all given filters. */
  AND?: TranslationSubscriptionWhereInput[] | null;
  /** Logical OR on all given filters. */
  OR?: TranslationSubscriptionWhereInput[] | null;
  /** Logical NOT on all given filters combined by AND. */
  NOT?: TranslationSubscriptionWhereInput[] | null;
  /** The subscription event gets dispatched when it's listed in mutation_in */
  mutation_in?: MutationType[] | null;
  /** The subscription event gets only dispatched when one of the updated fields names is included in this list */
  updatedFields_contains?: string | null;
  /** The subscription event gets only dispatched when all of the field names included in this list have been updated */
  updatedFields_contains_every?: string[] | null;
  /** The subscription event gets only dispatched when some of the field names included in this list have been updated */
  updatedFields_contains_some?: string[] | null;

  node?: TranslationWhereInput | null;
}

// ====================================================
// Arguments
// ====================================================

export interface LanguagesQueryArgs {
  where?: LanguageWhereInput | null;

  orderBy?: LanguageOrderByInput | null;

  skip?: number | null;

  after?: string | null;

  before?: string | null;

  first?: number | null;

  last?: number | null;
}
export interface MarketingStoriesQueryArgs {
  where?: MarketingStoryWhereInput | null;

  orderBy?: MarketingStoryOrderByInput | null;

  skip?: number | null;

  after?: string | null;

  before?: string | null;

  first?: number | null;

  last?: number | null;
}
export interface GifsQueryArgs {
  query: string;
}
export interface FileQueryArgs {
  key: string;
}
export interface DontPanicSessionQueryArgs {
  id: string;
}
export interface TranslationsLanguageArgs {
  where?: TranslationWhereInput | null;

  orderBy?: TranslationOrderByInput | null;

  skip?: number | null;

  after?: string | null;

  before?: string | null;

  first?: number | null;

  last?: number | null;
}
export interface TranslationsKeyArgs {
  where?: TranslationWhereInput | null;

  orderBy?: TranslationOrderByInput | null;

  skip?: number | null;

  after?: string | null;

  before?: string | null;

  first?: number | null;

  last?: number | null;
}
export interface AssetMarketingStoryAssetArgs {
  where?: MarketingStoryWhereInput | null;

  orderBy?: MarketingStoryOrderByInput | null;

  skip?: number | null;

  after?: string | null;

  before?: string | null;

  first?: number | null;

  last?: number | null;
}
export interface UrlAssetArgs {
  transformation?: AssetTransformationInput | null;
}
export interface CreateSessionMutationArgs {
  campaign?: CampaignInput | null;

  trackingId?: Uuid | null;
}
export interface CreateOfferMutationArgs {
  details: OfferInput;
}
export interface SignOfferMutationArgs {
  details: SignInput;
}
export interface UploadFileMutationArgs {
  file: Upload;
}
export interface SelectCashbackOptionMutationArgs {
  id: string;
}
export interface SendChatTextResponseMutationArgs {
  input: ChatResponseTextInput;
}
export interface SendChatSingleSelectResponseMutationArgs {
  input: ChatResponseSingleSelectInput;
}
export interface SendChatFileResponseMutationArgs {
  input: ChatResponseFileInput;
}
export interface SendChatAudioResponseMutationArgs {
  input: ChatResponseAudioInput;
}
export interface UpdateEmailMutationArgs {
  input: string;
}
export interface UpdatePhoneNumberMutationArgs {
  input: string;
}
export interface RequestStartDateMutationArgs {
  requestedStartDate: LocalDate;
}
export interface CreateDontPanicSessionMutationArgs {
  name: string;

  lastName?: string | null;

  email?: string | null;

  currentInsurer?: string | null;
}
export interface AddDontPanicChatMessageMutationArgs {
  sessionId: string;

  who: string;

  text: string;

  isHedvig: boolean;

  hedvigsSecret?: string | null;

  type?: string | null;
}
export interface MessagesSubscriptionArgs {
  mostRecentTimestamp: string;
}
export interface CurrentChatResponseSubscriptionArgs {
  mostRecentTimestamp: string;
}
export interface ChatStateSubscriptionArgs {
  mostRecentTimestamp: string;
}

// ====================================================
// Enums
// ====================================================

export enum Status {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  ARCHIVED = 'ARCHIVED',
}

export enum Project {
  Web = 'Web',
  WebOnboarding = 'WebOnboarding',
  App = 'App',
  All = 'All',
  Android = 'Android',
  IOS = 'IOS',
}

export enum LanguageOrderByInput {
  status_ASC = 'status_ASC',
  status_DESC = 'status_DESC',
  updatedAt_ASC = 'updatedAt_ASC',
  updatedAt_DESC = 'updatedAt_DESC',
  createdAt_ASC = 'createdAt_ASC',
  createdAt_DESC = 'createdAt_DESC',
  id_ASC = 'id_ASC',
  id_DESC = 'id_DESC',
  code_ASC = 'code_ASC',
  code_DESC = 'code_DESC',
  name_ASC = 'name_ASC',
  name_DESC = 'name_DESC',
}

export enum TranslationOrderByInput {
  status_ASC = 'status_ASC',
  status_DESC = 'status_DESC',
  updatedAt_ASC = 'updatedAt_ASC',
  updatedAt_DESC = 'updatedAt_DESC',
  createdAt_ASC = 'createdAt_ASC',
  createdAt_DESC = 'createdAt_DESC',
  id_ASC = 'id_ASC',
  id_DESC = 'id_DESC',
  project_ASC = 'project_ASC',
  project_DESC = 'project_DESC',
  text_ASC = 'text_ASC',
  text_DESC = 'text_DESC',
}

export enum HedvigColor {
  Pink = 'Pink',
  Turquoise = 'Turquoise',
  Purple = 'Purple',
  DarkPurple = 'DarkPurple',
  BlackPurple = 'BlackPurple',
  DarkGray = 'DarkGray',
  LightGray = 'LightGray',
  White = 'White',
  Black = 'Black',
  OffBlack = 'OffBlack',
  OffWhite = 'OffWhite',
}

export enum MarketingStoryOrderByInput {
  status_ASC = 'status_ASC',
  status_DESC = 'status_DESC',
  updatedAt_ASC = 'updatedAt_ASC',
  updatedAt_DESC = 'updatedAt_DESC',
  createdAt_ASC = 'createdAt_ASC',
  createdAt_DESC = 'createdAt_DESC',
  id_ASC = 'id_ASC',
  id_DESC = 'id_DESC',
  duration_ASC = 'duration_ASC',
  duration_DESC = 'duration_DESC',
  importance_ASC = 'importance_ASC',
  importance_DESC = 'importance_DESC',
  backgroundColor_ASC = 'backgroundColor_ASC',
  backgroundColor_DESC = 'backgroundColor_DESC',
}

export enum ImageFit {
  clip = 'clip',
  crop = 'crop',
  scale = 'scale',
  max = 'max',
}

export enum DocumentFileTypes {
  jpg = 'jpg',
  odp = 'odp',
  ods = 'ods',
  odt = 'odt',
  png = 'png',
  svg = 'svg',
  txt = 'txt',
  webp = 'webp',
  docx = 'docx',
  html = 'html',
  pdf = 'pdf',
  doc = 'doc',
  xlsx = 'xlsx',
  xls = 'xls',
  pptx = 'pptx',
  ppt = 'ppt',
}

export enum InsuranceStatus {
  PENDING = 'PENDING',
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  INACTIVE_WITH_START_DATE = 'INACTIVE_WITH_START_DATE',
  TERMINATED = 'TERMINATED',
}

export enum InsuranceType {
  RENT = 'RENT',
  BRF = 'BRF',
  STUDENT_RENT = 'STUDENT_RENT',
  STUDENT_BRF = 'STUDENT_BRF',
}

export enum BankIdStatus {
  pending = 'pending',
  failed = 'failed',
  complete = 'complete',
}

export enum SignState {
  INITIATED = 'INITIATED',
  IN_PROGRESS = 'IN_PROGRESS',
  FAILED = 'FAILED',
  COMPLETED = 'COMPLETED',
}

export enum MessageBodyChoicesLinkView {
  OFFER = 'OFFER',
  DASHBOARD = 'DASHBOARD',
}

export enum DirectDebitStatus {
  ACTIVE = 'ACTIVE',
  PENDING = 'PENDING',
  NEEDS_SETUP = 'NEEDS_SETUP',
}

export enum RegisterAccountProcessingStatus {
  NOT_INITIATED = 'NOT_INITIATED',
  INITIATED = 'INITIATED',
  REQUESTED = 'REQUESTED',
  IN_PROGRESS = 'IN_PROGRESS',
  CONFIRMED = 'CONFIRMED',
  CANCELLED = 'CANCELLED',
}

export enum CancelDirectDebitStatus {
  ACCEPTED = 'ACCEPTED',
  DECLINED_MISSING_TOKEN = 'DECLINED_MISSING_TOKEN',
  DECLINED_MISSING_REQUEST = 'DECLINED_MISSING_REQUEST',
}

export enum OfferStatus {
  SUCCESS = 'SUCCESS',
  FAIL = 'FAIL',
}

export enum PerilOrderByInput {
  status_ASC = 'status_ASC',
  status_DESC = 'status_DESC',
  updatedAt_ASC = 'updatedAt_ASC',
  updatedAt_DESC = 'updatedAt_DESC',
  createdAt_ASC = 'createdAt_ASC',
  createdAt_DESC = 'createdAt_DESC',
  id_ASC = 'id_ASC',
  id_DESC = 'id_DESC',
}

export enum AssetOrderByInput {
  status_ASC = 'status_ASC',
  status_DESC = 'status_DESC',
  updatedAt_ASC = 'updatedAt_ASC',
  updatedAt_DESC = 'updatedAt_DESC',
  createdAt_ASC = 'createdAt_ASC',
  createdAt_DESC = 'createdAt_DESC',
  id_ASC = 'id_ASC',
  id_DESC = 'id_DESC',
  handle_ASC = 'handle_ASC',
  handle_DESC = 'handle_DESC',
  fileName_ASC = 'fileName_ASC',
  fileName_DESC = 'fileName_DESC',
  height_ASC = 'height_ASC',
  height_DESC = 'height_DESC',
  width_ASC = 'width_ASC',
  width_DESC = 'width_DESC',
  size_ASC = 'size_ASC',
  size_DESC = 'size_DESC',
  mimeType_ASC = 'mimeType_ASC',
  mimeType_DESC = 'mimeType_DESC',
}

export enum ColorOrderByInput {
  updatedAt_ASC = 'updatedAt_ASC',
  updatedAt_DESC = 'updatedAt_DESC',
  createdAt_ASC = 'createdAt_ASC',
  createdAt_DESC = 'createdAt_DESC',
  id_ASC = 'id_ASC',
  id_DESC = 'id_DESC',
}

export enum LocationOrderByInput {
  updatedAt_ASC = 'updatedAt_ASC',
  updatedAt_DESC = 'updatedAt_DESC',
  createdAt_ASC = 'createdAt_ASC',
  createdAt_DESC = 'createdAt_DESC',
  id_ASC = 'id_ASC',
  id_DESC = 'id_DESC',
}

export enum KeyOrderByInput {
  status_ASC = 'status_ASC',
  status_DESC = 'status_DESC',
  updatedAt_ASC = 'updatedAt_ASC',
  updatedAt_DESC = 'updatedAt_DESC',
  createdAt_ASC = 'createdAt_ASC',
  createdAt_DESC = 'createdAt_DESC',
  id_ASC = 'id_ASC',
  id_DESC = 'id_DESC',
  value_ASC = 'value_ASC',
  value_DESC = 'value_DESC',
  description_ASC = 'description_ASC',
  description_DESC = 'description_DESC',
}

export enum PerilCategoryOrderByInput {
  status_ASC = 'status_ASC',
  status_DESC = 'status_DESC',
  updatedAt_ASC = 'updatedAt_ASC',
  updatedAt_DESC = 'updatedAt_DESC',
  createdAt_ASC = 'createdAt_ASC',
  createdAt_DESC = 'createdAt_DESC',
  id_ASC = 'id_ASC',
  id_DESC = 'id_DESC',
  name_ASC = 'name_ASC',
  name_DESC = 'name_DESC',
}

export enum MutationType {
  CREATED = 'CREATED',
  UPDATED = 'UPDATED',
  DELETED = 'DELETED',
}

export enum Locale {
  EN = 'EN',
}

export enum CacheControlScope {
  PUBLIC = 'PUBLIC',
  PRIVATE = 'PRIVATE',
}

// ====================================================
// Unions
// ====================================================

export type MessageBody =
  | MessageBodySingleSelect
  | MessageBodyMultipleSelect
  | MessageBodyText
  | MessageBodyNumber
  | MessageBodyAudio
  | MessageBodyBankIdCollect
  | MessageBodyFile
  | MessageBodyParagraph
  | MessageBodyUndefined;

export type MessageBodyChoices =
  | MessageBodyChoicesUndefined
  | MessageBodyChoicesSelection
  | MessageBodyChoicesLink;

// ====================================================
// END: Typescript template
// ====================================================

// ====================================================
// Documents
// ====================================================

export type SendChatFileResponseVariables = {
  input: ChatResponseFileInput;
};

export type SendChatFileResponseMutation = {
  __typename?: 'Mutation';

  sendChatFileResponse: boolean;
};

export type MessagesVariables = {};

export type MessagesQuery = {
  __typename?: 'Query';

  directDebitStatus: DirectDebitStatus;
};

export type NewOfferVariables = {};

export type NewOfferQuery = {
  __typename?: 'Query';

  insurance: NewOfferInsurance;
};

export type NewOfferInsurance = {
  __typename?: 'Insurance';

  activeFrom?: LocalDate | null;

  requestedStartDate?: LocalDate | null;

  address?: string | null;

  monthlyCost?: number | null;

  personsInHousehold?: number | null;

  insuredAtOtherCompany?: boolean | null;

  type?: InsuranceType | null;
};

export type OfferPerilsVariables = {};

export type OfferPerilsQuery = {
  __typename?: 'Query';

  insurance: OfferPerilsInsurance;
};

export type OfferPerilsInsurance = {
  __typename?: 'Insurance';

  id?: Uuid | null;

  activeFrom?: LocalDate | null;

  address?: string | null;

  type?: InsuranceType | null;

  perilCategories?: OfferPerilsPerilCategories[] | null;
};

export type OfferPerilsPerilCategories = {
  __typename?: 'PerilCategory';

  title?: string | null;

  description?: string | null;

  iconUrl?: string | null;

  perils?: OfferPerilsPerils[] | null;
};

export type OfferPerilsPerils = {
  __typename?: 'Peril';

  id?: string | null;

  title?: string | null;

  imageUrl?: string | null;

  description?: string | null;
};

export type DirectDebitRegistrationVariables = {};

export type DirectDebitRegistrationMutation = {
  __typename?: 'Mutation';

  startDirectDebitRegistration: Url;
};

export type CashbackOptionsVariables = {};

export type CashbackOptionsQuery = {
  __typename?: 'Query';

  cashbackOptions: CashbackOptionsCashbackOptions[];
};

export type CashbackOptionsCashbackOptions = {
  __typename?: 'Cashback';

  id?: string | null;

  name?: string | null;
};

export type SelectCashbackOptionVariables = {
  id: string;
};

export type SelectCashbackOptionMutation = {
  __typename?: 'Mutation';

  selectCashbackOption: SelectCashbackOptionSelectCashbackOption;
};

export type SelectCashbackOptionSelectCashbackOption = {
  __typename?: 'Cashback';

  id?: string | null;

  name?: string | null;

  imageUrl?: string | null;
};

import * as ReactApollo from 'react-apollo';
import * as React from 'react';

import gql from 'graphql-tag';

// ====================================================
// Components
// ====================================================

export const SendChatFileResponseDocument = gql`
  mutation SendChatFileResponse($input: ChatResponseFileInput!) {
    sendChatFileResponse(input: $input)
  }
`;
export class SendChatFileResponseComponent extends React.Component<
  Partial<
    ReactApollo.MutationProps<
      SendChatFileResponseMutation,
      SendChatFileResponseVariables
    >
  >
> {
  render() {
    return (
      <ReactApollo.Mutation<
        SendChatFileResponseMutation,
        SendChatFileResponseVariables
      >
        mutation={SendChatFileResponseDocument}
        {...this['props'] as any}
      />
    );
  }
}
export function SendChatFileResponseHOC<
  TProps = any,
  OperationOptions = ReactApollo.OperationOption<
    TProps,
    SendChatFileResponseMutation,
    SendChatFileResponseVariables
  >
>(operationOptions: OperationOptions) {
  return ReactApollo.graphql<
    TProps,
    SendChatFileResponseMutation,
    SendChatFileResponseVariables
  >(SendChatFileResponseDocument, operationOptions);
}
export const MessagesDocument = gql`
  query Messages {
    directDebitStatus
  }
`;
export class MessagesComponent extends React.Component<
  Partial<ReactApollo.QueryProps<MessagesQuery, MessagesVariables>>
> {
  render() {
    return (
      <ReactApollo.Query<MessagesQuery, MessagesVariables>
        query={MessagesDocument}
        {...this['props'] as any}
      />
    );
  }
}
export function MessagesHOC<
  TProps = any,
  OperationOptions = ReactApollo.OperationOption<
    TProps,
    MessagesQuery,
    MessagesVariables
  >
>(operationOptions: OperationOptions) {
  return ReactApollo.graphql<TProps, MessagesQuery, MessagesVariables>(
    MessagesDocument,
    operationOptions,
  );
}
export const NewOfferDocument = gql`
  query NewOffer {
    insurance {
      activeFrom
      requestedStartDate
      address
      monthlyCost
      personsInHousehold
      insuredAtOtherCompany
      type
    }
  }
`;
export class NewOfferComponent extends React.Component<
  Partial<ReactApollo.QueryProps<NewOfferQuery, NewOfferVariables>>
> {
  render() {
    return (
      <ReactApollo.Query<NewOfferQuery, NewOfferVariables>
        query={NewOfferDocument}
        {...this['props'] as any}
      />
    );
  }
}
export function NewOfferHOC<
  TProps = any,
  OperationOptions = ReactApollo.OperationOption<
    TProps,
    NewOfferQuery,
    NewOfferVariables
  >
>(operationOptions: OperationOptions) {
  return ReactApollo.graphql<TProps, NewOfferQuery, NewOfferVariables>(
    NewOfferDocument,
    operationOptions,
  );
}
export const OfferPerilsDocument = gql`
  query OfferPerils {
    insurance {
      id
      activeFrom
      address
      type
      perilCategories {
        title
        description
        iconUrl
        perils {
          id
          title
          imageUrl
          description
        }
      }
    }
  }
`;
export class OfferPerilsComponent extends React.Component<
  Partial<ReactApollo.QueryProps<OfferPerilsQuery, OfferPerilsVariables>>
> {
  render() {
    return (
      <ReactApollo.Query<OfferPerilsQuery, OfferPerilsVariables>
        query={OfferPerilsDocument}
        {...this['props'] as any}
      />
    );
  }
}
export function OfferPerilsHOC<
  TProps = any,
  OperationOptions = ReactApollo.OperationOption<
    TProps,
    OfferPerilsQuery,
    OfferPerilsVariables
  >
>(operationOptions: OperationOptions) {
  return ReactApollo.graphql<TProps, OfferPerilsQuery, OfferPerilsVariables>(
    OfferPerilsDocument,
    operationOptions,
  );
}
export const DirectDebitRegistrationDocument = gql`
  mutation DirectDebitRegistration {
    startDirectDebitRegistration
  }
`;
export class DirectDebitRegistrationComponent extends React.Component<
  Partial<
    ReactApollo.MutationProps<
      DirectDebitRegistrationMutation,
      DirectDebitRegistrationVariables
    >
  >
> {
  render() {
    return (
      <ReactApollo.Mutation<
        DirectDebitRegistrationMutation,
        DirectDebitRegistrationVariables
      >
        mutation={DirectDebitRegistrationDocument}
        {...this['props'] as any}
      />
    );
  }
}
export function DirectDebitRegistrationHOC<
  TProps = any,
  OperationOptions = ReactApollo.OperationOption<
    TProps,
    DirectDebitRegistrationMutation,
    DirectDebitRegistrationVariables
  >
>(operationOptions: OperationOptions) {
  return ReactApollo.graphql<
    TProps,
    DirectDebitRegistrationMutation,
    DirectDebitRegistrationVariables
  >(DirectDebitRegistrationDocument, operationOptions);
}
export const CashbackOptionsDocument = gql`
  query CashbackOptions {
    cashbackOptions {
      id
      name
    }
  }
`;
export class CashbackOptionsComponent extends React.Component<
  Partial<
    ReactApollo.QueryProps<CashbackOptionsQuery, CashbackOptionsVariables>
  >
> {
  render() {
    return (
      <ReactApollo.Query<CashbackOptionsQuery, CashbackOptionsVariables>
        query={CashbackOptionsDocument}
        {...this['props'] as any}
      />
    );
  }
}
export function CashbackOptionsHOC<
  TProps = any,
  OperationOptions = ReactApollo.OperationOption<
    TProps,
    CashbackOptionsQuery,
    CashbackOptionsVariables
  >
>(operationOptions: OperationOptions) {
  return ReactApollo.graphql<
    TProps,
    CashbackOptionsQuery,
    CashbackOptionsVariables
  >(CashbackOptionsDocument, operationOptions);
}
export const SelectCashbackOptionDocument = gql`
  mutation SelectCashbackOption($id: ID!) {
    selectCashbackOption(id: $id) {
      id
      name
      imageUrl
    }
  }
`;
export class SelectCashbackOptionComponent extends React.Component<
  Partial<
    ReactApollo.MutationProps<
      SelectCashbackOptionMutation,
      SelectCashbackOptionVariables
    >
  >
> {
  render() {
    return (
      <ReactApollo.Mutation<
        SelectCashbackOptionMutation,
        SelectCashbackOptionVariables
      >
        mutation={SelectCashbackOptionDocument}
        {...this['props'] as any}
      />
    );
  }
}
export function SelectCashbackOptionHOC<
  TProps = any,
  OperationOptions = ReactApollo.OperationOption<
    TProps,
    SelectCashbackOptionMutation,
    SelectCashbackOptionVariables
  >
>(operationOptions: OperationOptions) {
  return ReactApollo.graphql<
    TProps,
    SelectCashbackOptionMutation,
    SelectCashbackOptionVariables
  >(SelectCashbackOptionDocument, operationOptions);
}
