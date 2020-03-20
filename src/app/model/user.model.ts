export class User {
    constructor(
        public accountId: number,
        public username: string,
        public firstName: string,
        public lastName: string,
        public password: string,
        public email: string,
        public phoneNumber: string,
        public bornDate: string,
        public active: boolean,
        public gender: string
  ) {}
  }
  